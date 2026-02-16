package com.hanhyo.commitlog.data.repository

import com.hanhyo.commitlog.data.mapper.MonthlyReviewMapper
import com.hanhyo.commitlog.domain.model.AIMood
import com.hanhyo.commitlog.domain.model.AiAnalysisResult
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.CommitAnalysis
import com.hanhyo.commitlog.domain.model.CommitTitle
import com.hanhyo.commitlog.domain.model.DifficultyLevel
import com.hanhyo.commitlog.domain.model.LearnedContent
import com.hanhyo.commitlog.domain.model.LearningTag
import com.hanhyo.commitlog.domain.model.MonthlyReview
import com.hanhyo.commitlog.domain.repository.AiAnalysisRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiAnalysisRepositoryImpl @Inject constructor(
    // TODO: OpenAIApi
) : AiAnalysisRepository {

    override suspend fun analyzeCommit(
        title: CommitTitle,
        learnedToday: LearnedContent,
        difficulties: String?,
        tomorrowPlan: String?
    ): AiAnalysisResult {
        // TODO: 실제 OpenAI API 호출

        // Mock 구현
        val mockTags = extractMockTags(title.value, learnedToday.value)
        val mockMood = determineMockMood(title.value, learnedToday.value)

        return AiAnalysisResult(
            analysis = CommitAnalysis(
                mood = mockMood,
                moodScore = 75,
                difficultyLevel = DifficultyLevel.NORMAL,
                comment = "꾸준한 학습이 인상적입니다! 계속 이어가세요."
            ),
            tags = mockTags
        )
    }

    override suspend fun generateMonthlyReview(
        commits: List<Commit>,
        year: Int,
        month: Int
    ): MonthlyReview {
        // TODO: 실제 OpenAI API 호출

        val aiSummary = generateMockSummary(commits, year, month)

        return MonthlyReviewMapper.create(
            commits = commits,
            year = year,
            month = month,
            aiSummary = aiSummary
        )
    }

    /**
     * Mock 태그 추출
     * 실제로는 AI가 추출
     */
    private fun extractMockTags(title: String, learnedToday: String): Set<LearningTag> {
        val text = "$title $learnedToday".lowercase()
        val extractedTags = mutableSetOf<String>()

        val keywords = mapOf(
            "kotlin" to "kotlin",
            "코틀린" to "kotlin",
            "android" to "android",
            "안드로이드" to "android",
            "compose" to "compose",
            "컴포즈" to "compose",
            "java" to "java",
            "sql" to "sql",
            "database" to "database",
            "알고리즘" to "algorithm",
            "algorithm" to "algorithm",
            "api" to "api",
            "coroutine" to "coroutine",
            "코루틴" to "coroutine",
            "room" to "room",
            "retrofit" to "retrofit",
            "hilt" to "hilt",
            "mvvm" to "mvvm"
        )

        keywords.forEach { (key, tag) ->
            if (text.contains(key) && extractedTags.size < 5) {
                extractedTags.add(tag)
            }
        }

        return LearningTag.fromStringList(extractedTags.toList())
    }

    /**
     * Mock Mood 결정: 실제로는 AI가 분석
     */
    private fun determineMockMood(title: String, learnedToday: String): AIMood {
        val text = "$title $learnedToday".lowercase()

        return when {
            text.contains("완성") || text.contains("성공") || text.contains("해결") ->
                AIMood.PRODUCTIVE

            text.contains("배웠다") || text.contains("이해") || text.contains("공부") ->
                AIMood.CURIOUS

            text.contains("집중") || text.contains("몰입") ->
                AIMood.FOCUSED

            text.contains("어렵") || text.contains("힘들") ->
                AIMood.CONFUSED

            text.contains("피곤") || text.contains("지침") ->
                AIMood.TIRED

            text.contains("아이디어") || text.contains("영감") ->
                AIMood.INSPIRED

            text.contains("극복") ->
                AIMood.RELIEVED

            else ->
                AIMood.NORMAL
        }
    }

    /**
     * Mock 회고 요약 생성: 실제로는 AI가 생성
     */
    private fun generateMockSummary(
        commits: List<Commit>,
        year: Int,
        month: Int
    ): String {
        val totalCount = commits.size
        val analyzedCommits = commits.filter { it.isAnalyzed() }

        val moodCounts = analyzedCommits
            .mapNotNull { it.analysis?.mood }
            .groupingBy { it }
            .eachCount()

        val mostFrequentMood = moodCounts.maxByOrNull { it.value }?.key

        val avgScore = analyzedCommits
            .mapNotNull { it.analysis?.moodScore }
            .average()
            .takeIf { !it.isNaN() } ?: 0.0

        val tagCounts = commits
            .flatMap { it.tags }
            .groupingBy { it }
            .eachCount()

        val topTags = tagCounts.entries
            .sortedByDescending { it.value }
            .take(3)
            .map { it.key.value }

        return buildString {
            appendLine("[한 달 요약]")
            appendLine("${year}년 ${month}월에는 총 ${totalCount}개의 커밋을 작성하셨습니다.")
            appendLine("평균 학습 점수는 ${avgScore.toInt()}/100으로 꾸준한 학습 흐름을 보여주셨습니다.")
            mostFrequentMood?.let {
                appendLine("가장 많이 나타난 학습 상태는 '${it.displayNameKo}'입니다.")
            }

            appendLine()
            appendLine("[학습 패턴 분석]")
            if (topTags.isNotEmpty()) {
                appendLine("이번 달에는 ${topTags.joinToString(", ")} 분야에 집중하셨습니다.")
            }
            appendLine("주차별로 고르게 학습하는 패턴을 보였습니다.")

            appendLine()
            appendLine("[다음 달을 위한 제안]")
            appendLine("현재의 학습 페이스를 유지하면서, 새로운 분야로도 확장해보세요.")
            appendLine("어려웠던 부분들을 복습하는 시간을 가져보시는 것을 추천드립니다.")
        }
    }
}
