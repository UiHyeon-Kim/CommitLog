package com.hanhyo.commitlog.data.source.remote.api

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber

/**
 * Gemini REST API 기반 AI 서비스
 *
 * @param apiKey Gemini API Key
 * @param client OkHttpClient (공유)
 */
class GeminiAiService(
    private val apiKey: String,
    private val client: OkHttpClient,
) : AiService {

    companion object {
        private const val BASE_URL_FORMAT =
            "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent"
        private const val MODEL_DEFAULT = "gemini-2.5-flash"
        private const val MODEL_HEAVY = "gemini-pro-latest"

        private const val DEFAULT_TEMPERATURE = 0.7
        private const val DEFAULT_MAX_TOKENS = 2048
        private const val DEFAULT_TOP_K = 40
        private const val DEFAULT_TOP_P = 0.95
    }

    override suspend fun analyzeCommit(
        title: String,
        learned: String,
        difficulty: String?,
        tomorrow: String?,
    ): String = withContext(Dispatchers.IO) {
        try {
            val prompt = buildAnalyzePrompt(title, learned, difficulty, tomorrow)
            callGemini(
                prompt = prompt,
                modelName = MODEL_DEFAULT,
                temperature = 0.5,  // 커밋 분석은 일관성 우선
                maxTokens = 2048
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Timber.e(e, "Gemini 커밋 분석 실패")
            throw AiServiceException("커밋 분석 중 오류가 발생했습니다: ${e.message}", e)
        }
    }

    override suspend fun generateMonthlyReview(prompt: String): String =
        withContext(Dispatchers.IO) {
            try {
                callGemini(
                    prompt = prompt,
                    modelName = MODEL_HEAVY,
                    temperature = 0.9,  // 회고는 창의성 우선
                    maxTokens = 4096
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, "Gemini 월간 회고 생성 실패")
                throw AiServiceException("월간 회고 생성 중 오류가 발생했습니다: ${e.message}", e)
            }
        }

    /**
     * Gemini API 호출 공통 메서드
     */
    private fun callGemini(
        prompt: String,
        modelName: String,
        temperature: Double = DEFAULT_TEMPERATURE,
        maxTokens: Int = DEFAULT_MAX_TOKENS
    ): String {
        if (apiKey.isBlank()) {
            throw AiServiceException("Gemini API 키가 누락되었습니다.")
        }
        val requestBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", temperature)
                put("maxOutputTokens", maxTokens)
                put("topK", DEFAULT_TOP_K)
                put("topP", DEFAULT_TOP_P)
            })
            // Safety Settings (필요시)
            put("safetySettings", JSONArray().apply {
                // 학습 내용은 대부분 안전하므로 필터 완화
                listOf(
                    "HARM_CATEGORY_HATE_SPEECH",
                    "HARM_CATEGORY_SEXUALLY_EXPLICIT",
                    "HARM_CATEGORY_DANGEROUS_CONTENT",
                    "HARM_CATEGORY_HARASSMENT"
                ).forEach { category ->
                    put(JSONObject().apply {
                        put("category", category)
                        put("threshold", "BLOCK_ONLY_HIGH")
                    })
                }
            })
        }

        val url = String.format(BASE_URL_FORMAT, modelName)
        val request = Request.Builder()
            .url("$url?key=$apiKey")
            .addHeader("Content-Type", "application/json")
            .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        return client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errorBody = response.body?.string() ?: "Unknown error"
                Timber.e("Gemini API error: ${response.code} - $errorBody")

                throw when (response.code) {
                    400 -> AiServiceException("잘못된 요청입니다")
                    401 -> AiServiceException("API 키가 유효하지 않습니다")
                    403 -> AiServiceException("API 접근이 거부되었습니다")
                    429 -> AiServiceException("API 사용량을 초과했습니다. 잠시 후 다시 시도해주세요")
                    500, 503 -> AiServiceException("AI 서버에 일시적인 문제가 있습니다")
                    else -> AiServiceException("AI 분석 중 오류가 발생했습니다 (${response.code})")
                }
            }

            val responseBody = response.body?.string()
                ?: throw AiServiceException("AI 응답이 비어있습니다")

            try {
                parseGeminiResponse(responseBody)
            } catch (e: Exception) {
                Timber.e(e, "Gemini 응답 파싱 실패: $responseBody")
                throw AiServiceException("AI 응답 형식이 올바르지 않습니다", e)
            }
        }
    }

    /**
     * Gemini 응답에서 텍스트 추출
     */
    private fun parseGeminiResponse(responseBody: String): String {
        val jsonResponse = JSONObject(responseBody)

        // candidates 배열 확인
        if (!jsonResponse.has("candidates")) {
            throw AiServiceException("AI 응답에 candidates가 없습니다")
        }

        val candidates = jsonResponse.getJSONArray("candidates")
        if (candidates.length() == 0) {
            throw AiServiceException("AI가 응답을 생성하지 못했습니다")
        }

        val candidate = candidates.getJSONObject(0)

        // finishReason 확인
        val finishReason = candidate.optString("finishReason", "")
        if (finishReason == "SAFETY") {
            throw AiServiceException("안전 필터에 의해 차단되었습니다")
        }

        return candidate
            .getJSONObject("content")
            .getJSONArray("parts")
            .getJSONObject(0)
            .getString("text")
    }

    /**
     * 커밋 분석 프롬프트
     */
    private fun buildAnalyzePrompt(
        title: String,
        learned: String,
        difficulty: String?,
        tomorrow: String?,
    ): String = """
Analyze this dev log and return ONLY a strict JSON object. No reasoning, no markdown formatting.
Title: $title
Learned: $learned
Difficulty: ${difficulty ?: "None"}
Tomorrow: ${tomorrow ?: "None"}

Requirements:
- mood: Exact match from [CURIOUS, FOCUSED, PRODUCTIVE, CONFUSED, TIRED, RELIEVED, INSPIRED, NORMAL].
- moodScore: Integer 1-100.
- difficultyLevel: Exact match from ["매우 쉬움", "쉬움", "보통", "어려움", "매우 어려움"].
- tags: 2-5 English keywords (e.g., ["Jetpack Compose", "Coroutines"]).
- comment: 1~2 insightful Korean sentences (max 150 chars) providing specific, helpful feedback or encouragement based on the dev log.

Response format:
{"mood":"FOCUSED","moodScore":75,"difficultyLevel":"보통","comment":"꾸준한 학습이 빛을 발하네요!","tags":["Jetpack Compose"]}
""".trimIndent()
}

/**
 * AI 서비스 예외
 */
class AiServiceException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
