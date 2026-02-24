package com.hanhyo.commitlog.data.source.remote.api

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
 * OpenAI REST API 기반 AI 서비스
 *
 * 참고: 현재 기본 프로바이더는 Gemini이며, OpenAI로 변경하려면
 * NetworkModule에서 provideAiService()의 반환값만 교체하면 됩니다.
 *
 * @param apiKey OpenAI API Key
 * @param client OkHttpClient (공유)
 */
class OpenAiService(
    private val apiKey: String,
    private val client: OkHttpClient,
) : AiService {

    companion object {
        private const val BASE_URL = "https://api.openai.com/v1/chat/completions"
        private const val MODEL = "gpt-3.5-turbo"
    }

    override suspend fun analyzeCommit(
        title: String,
        learned: String,
        difficulty: String?,
        tomorrow: String?,
    ): String = withContext(Dispatchers.IO) {
        val prompt = buildAnalyzePrompt(title, learned, difficulty, tomorrow)
        callOpenAI(prompt, maxTokens = 300)
    }

    override suspend fun generateMonthlyReview(prompt: String): String =
        withContext(Dispatchers.IO) {
            callOpenAI(prompt, maxTokens = 1024)
        }

    /**
     * OpenAI Chat Completions API 호출 공통 메서드
     */
    private fun callOpenAI(prompt: String, maxTokens: Int): String {
        val requestBody = JSONObject().apply {
            put("model", MODEL)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
            put("max_tokens", maxTokens)
            put("temperature", 0.7)
        }

        val request = Request.Builder()
            .url(BASE_URL)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()
            ?: throw Exception("OpenAI API: 빈 응답")

        if (!response.isSuccessful) {
            Timber.e("OpenAI API error: ${response.code} - $responseBody")
            if (response.code == 429) {
                throw Exception("AI 분석 한도가 초과되었습니다. 잠시 후 다시 시도해주세요.")
            }
            throw Exception("OpenAI API 오류 (${response.code}): $responseBody")
        }

        val jsonResponse = JSONObject(responseBody)
        return jsonResponse
            .getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content")
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
