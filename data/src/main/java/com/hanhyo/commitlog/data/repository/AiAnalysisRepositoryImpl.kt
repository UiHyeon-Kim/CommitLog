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
import com.hanhyo.commitlog.data.source.remote.AiService
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiAnalysisRepositoryImpl @Inject constructor(
    private val aiService: AiService,
) : AiAnalysisRepository {

    override suspend fun analyzeCommit(
        title: CommitTitle,
        learnedToday: LearnedContent,
        difficulties: String?,
        tomorrowPlan: String?
    ): AiAnalysisResult {
        val responseText = aiService.analyzeCommit(
            title = title.value,
            learned = learnedToday.value,
            difficulty = difficulties,
            tomorrow = tomorrowPlan,
        )

        return parseAnalysisResponse(responseText)
    }

    override suspend fun generateMonthlyReview(
        commits: List<Commit>,
        year: Int,
        month: Int
    ): MonthlyReview {
        val prompt = buildMonthlyReviewPrompt(commits, year, month)
        val aiSummary = aiService.generateMonthlyReview(prompt)

        return MonthlyReviewMapper.create(
            commits = commits,
            year = year,
            month = month,
            aiSummary = aiSummary
        )
    }

    /**
     * AI 응답 JSON을 AiAnalysisResult로 파싱
     */
    private fun parseAnalysisResponse(responseText: String): AiAnalysisResult {
        try {
            // AI 응답에서 JSON 추출 (```json ... ``` 감싸기 제거)
            val jsonString = responseText
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val json = JSONObject(jsonString)

            val mood = AIMood.fromName(json.optString("mood", "NORMAL"))
            val moodScore = json.optInt("moodScore", 50)
            val difficultyLevel = DifficultyLevel.fromDisplayName(
                json.optString("difficultyLevel", "보통")
            ) ?: DifficultyLevel.NORMAL
            val comment = json.optString("comment", "꾸준한 학습을 이어가세요!")

            // tags 파싱
            val tagsArray = json.optJSONArray("tags")
            val tags = if (tagsArray != null) {
                val tagList = mutableListOf<String>()
                for (i in 0 until tagsArray.length()) {
                    tagList.add(tagsArray.getString(i))
                }
                LearningTag.fromStringList(tagList)
            } else {
                emptySet()
            }

            return AiAnalysisResult(
                analysis = CommitAnalysis(
                    mood = mood,
                    moodScore = moodScore,
                    difficultyLevel = difficultyLevel,
                    comment = comment,
                ),
                tags = tags
            )
        } catch (e: Exception) {
            Timber.e(e, "AI 응답 파싱 실패: $responseText")
            // 파싱 실패 시 기본값 반환
            return AiAnalysisResult(
                analysis = CommitAnalysis(
                    mood = AIMood.NORMAL,
                    moodScore = 50,
                    difficultyLevel = DifficultyLevel.NORMAL,
                    comment = "분석 결과를 처리하지 못했습니다.",
                ),
                tags = emptySet()
            )
        }
    }

    /**
     * 월간 회고 프롬프트 생성
     * 사용자가 지정한 프롬프트 구조를 사용
     */
    private fun buildMonthlyReviewPrompt(
        commits: List<Commit>,
        year: Int,
        month: Int,
    ): String {
        val totalCommitCount = commits.size

        // Mood 분포
        val moodDistribution = commits
            .mapNotNull { it.analysis?.mood }
            .groupingBy { it.displayNameKo }
            .eachCount()
            .entries
            .joinToString(", ") { "${it.key}: ${it.value}건" }
            .ifEmpty { "데이터 없음" }

        // 주차별 커밋 수
        val weeklyCommitCount = commits
            .groupBy { commit ->
                val weekOfMonth = (commit.date.dayOfMonth - 1) / 7 + 1
                "${weekOfMonth}주차"
            }
            .entries
            .sortedBy { it.key }
            .joinToString(", ") { "${it.key}: ${it.value.size}건" }

        // 특이사항 분석
        val notablePatterns = buildList {
            val analyzedCommits = commits.filter { it.isAnalyzed() }
            if (analyzedCommits.isNotEmpty()) {
                val avgScore = analyzedCommits.mapNotNull { it.analysis?.moodScore }.average()
                add("평균 학습 점수: ${avgScore.toInt()}/100")
            }
            val topTags = commits.flatMap { it.tags }
                .groupingBy { it.value }
                .eachCount()
                .entries
                .sortedByDescending { it.value }
                .take(3)
            if (topTags.isNotEmpty()) {
                add("주요 학습 태그: ${topTags.joinToString(", ") { "${it.key}(${it.value}회)" }}")
            }
            val confusedCount = commits.count { it.analysis?.mood == AIMood.CONFUSED }
            if (confusedCount > 0) {
                add("혼란스러움 상태가 ${confusedCount}회 감지됨")
            }
        }.joinToString("; ").ifEmpty { "없음" }

        return """
당신은 개발자의 학습 기록을 바탕으로
월간 회고를 도와주는 AI 코치입니다.

아래에 주어진 학습 통계 데이터를 바탕으로
한국어로 월간 학습 회고를 작성해주세요.

다음 규칙을 반드시 지켜주세요.

1. 감정적인 위로보다는 학습 흐름과 패턴 분석에 집중합니다.
2. 결과를 과장하지 않고, 데이터에 근거해 설명합니다.
3. 회고는 아래의 3개 섹션으로 나누어 작성합니다.

[섹션 구성]

1. 한 달 요약
2. 학습 패턴 분석
3. 다음 달을 위한 제안

[작성 스타일]

- 객관적이고 차분한 톤
- 일기체가 아닌 회고 보고서 스타일
- 각 섹션은 3~5문장 이내
- 전체 분량은 너무 길지 않게 유지

[학습 통계 데이터]

- 기간: ${year}년 ${month}월
- 총 커밋 수: ${totalCommitCount}
- AI Mood 분포: $moodDistribution
- 주차별 커밋 수: $weeklyCommitCount
- 특이사항: $notablePatterns
""".trimIndent()
    }
}
