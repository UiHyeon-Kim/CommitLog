package com.hanhyo.commitlog.data.source.remote.api

/**
 * AI 서비스 인터페이스
 *
 * Gemini / OpenAI 등 AI 프로바이더를 교체할 수 있도록 추상화
 */
interface AiService {

    /**
     * 커밋 내용을 분석하여 JSON 응답을 반환
     *
     * @return AI가 생성한 JSON 문자열 (mood, moodScore, difficultyLevel, comment, tags)
     */
    suspend fun analyzeCommit(
        title: String,
        learned: String,
        difficulty: String?,
        tomorrow: String?,
    ): String

    /**
     * 월간 회고 프롬프트를 보내고, AI 응답 텍스트를 반환
     *
     * @return AI가 생성한 회고 텍스트
     */
    suspend fun generateMonthlyReview(prompt: String): String
}
