package com.hanhyo.commitlog.data.source.remote

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
        // gemini-1.5-flash-001 (Specific version)
        private const val BASE_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-001:generateContent"
    }

    override suspend fun analyzeCommit(
        title: String,
        learned: String,
        difficulty: String?,
        tomorrow: String?,
    ): String = withContext(Dispatchers.IO) {
        val prompt = buildAnalyzePrompt(title, learned, difficulty, tomorrow)
        callGemini(prompt)
    }

    override suspend fun generateMonthlyReview(prompt: String): String =
        withContext(Dispatchers.IO) {
            callGemini(prompt)
        }

    /**
     * Gemini API 호출 공통 메서드
     */
    private fun callGemini(prompt: String): String {
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
                put("temperature", 0.7)
                put("maxOutputTokens", 1024)
            })
        }

        val request = Request.Builder()
            .url("$BASE_URL?key=$apiKey")
            .addHeader("Content-Type", "application/json")
            .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()
            ?: throw Exception("Gemini API: 빈 응답")

        if (!response.isSuccessful) {
            Timber.e("Gemini API error: ${response.code} - $responseBody")
            if (response.code == 429) {
                throw Exception("AI 분석 한도가 초과되었습니다. 잠시 후 다시 시도해주세요.")
            }
            throw Exception("Gemini API 오류 (${response.code}): $responseBody")
        }

        val jsonResponse = JSONObject(responseBody)
        return jsonResponse
            .getJSONArray("candidates")
            .getJSONObject(0)
            .getJSONObject("content")
            .getJSONArray("parts")
            .getJSONObject(0)
            .getString("text")
    }

    private fun buildAnalyzePrompt(
        title: String,
        learned: String,
        difficulty: String?,
        tomorrow: String?,
    ): String = """
당신은 개발자 학습 코치입니다. 아래 TIL(Today I Learned) 내용을 분석해주세요.

[TIL 내용]
제목: $title
오늘 배운 것: $learned
어려웠던 점: ${difficulty ?: "없음"}
내일 할 일: ${tomorrow ?: "없음"}

[분석 요청]
다음 형식의 JSON으로만 응답해주세요. JSON 외의 다른 텍스트는 포함하지 마세요:
{
    "mood": "CURIOUS/FOCUSED/PRODUCTIVE/CONFUSED/TIRED/RELIEVED/INSPIRED/NORMAL 중 하나",
    "moodScore": 1-100 사이 정수,
    "difficultyLevel": "매우 쉬움/쉬움/보통/어려움/매우 어려움 중 하나",
    "comment": "격려나 조언 한 문장 (30자 이내)",
    "tags": ["학습 주제 태그 1~5개, 예: kotlin, compose, algorithm"]
}
""".trimIndent()
}
