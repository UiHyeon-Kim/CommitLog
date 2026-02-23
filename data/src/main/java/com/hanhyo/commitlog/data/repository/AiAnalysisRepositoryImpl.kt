package com.hanhyo.commitlog.data.repository

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.hanhyo.commitlog.data.mapper.MonthlyReviewMapper
import com.hanhyo.commitlog.data.source.remote.api.AiService
import com.hanhyo.commitlog.data.source.remote.api.AiServiceException
import com.hanhyo.commitlog.data.worker.AiAnalysisWorker
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
import com.hanhyo.commitlog.data.source.local.database.dao.MonthlyReviewDao
import org.json.JSONObject
import timber.log.Timber
import java.util.concurrent.CancellationException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiAnalysisRepositoryImpl @Inject constructor(
    private val aiService: AiService,
    private val workManager: WorkManager,
    private val monthlyReviewDao: MonthlyReviewDao
) : AiAnalysisRepository {

    override suspend fun analyzeCommit(
        title: CommitTitle,
        learnedToday: LearnedContent,
        difficulties: String?,
        tomorrowPlan: String?
    ): AiAnalysisResult {
        try {
            val responseText = aiService.analyzeCommit(
                title = title.value,
                learned = learnedToday.value,
                difficulty = difficulties,
                tomorrow = tomorrowPlan,
            )

            return parseAnalysisResponse(responseText)

        } catch (e: CancellationException) {
            throw e
        } catch (e: AiServiceException) {
            // AI 서비스 에러는 그대로 전파
            throw e
        } catch (e: Exception) {
            Timber.e(e, "커밋 분석 중 예상치 못한 오류")
            throw AiServiceException("커밋 분석 중 오류가 발생했습니다", e)
        }
    }

    override suspend fun scheduleAnalysis(commitId: Long) {
        val workRequest = OneTimeWorkRequestBuilder<AiAnalysisWorker>()
            .setInputData(workDataOf(AiAnalysisWorker.KEY_COMMIT_ID to commitId))
            .build()

        workManager.enqueue(workRequest)
    }

    override suspend fun generateMonthlyReview(
        commits: List<Commit>,
        year: Int,
        month: Int
    ): MonthlyReview {
        try {
            // 1. DB에 저장된 회고가 있는지 확인
            val savedEntity = monthlyReviewDao.getMonthlyReview(year, month)
            if (savedEntity != null) {
                return MonthlyReviewMapper.mapToDomain(savedEntity)
            }

            // 2. 없다면 AI에 요청하여 생성
            val prompt = buildMonthlyReviewPrompt(commits, year, month)
            val aiSummary = aiService.generateMonthlyReview(prompt)

            val review = MonthlyReviewMapper.create(
                commits = commits,
                year = year,
                month = month,
                aiSummary = aiSummary
            )

            // 3. DB에 저장 후 반환
            monthlyReviewDao.insertMonthlyReview(MonthlyReviewMapper.mapToEntity(review))
            return review

        } catch (e: CancellationException) {
            throw e
        } catch (e: AiServiceException) {
            throw e
        } catch (e: Exception) {
            Timber.e(e, "월간 회고 생성 중 예상치 못한 오류")
            throw AiServiceException("월간 회고 생성 중 오류가 발생했습니다", e)
        }
    }

    /** AI 응답 JSON을 AiAnalysisResult로 파싱 */
    private fun parseAnalysisResponse(responseText: String): AiAnalysisResult {
        try {
            // AI 응답에서 JSON 추출 (```json ... ``` 감싸기 제거)
            val jsonString = responseText
                .trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val json = JSONObject(jsonString)

            // 각 필드 파싱 (기본값 제공)
            val mood = json.optString("mood", "NORMAL")
                .let { AIMood.fromName(it) }

            val moodScore = json.optInt("moodScore", 50)
                .coerceIn(1, 100)

            val difficultyLevel = json.optString("difficultyLevel", "보통")
                .let { DifficultyLevel.fromDisplayName(it) ?: DifficultyLevel.NORMAL }

            val comment = json.optString("comment", "꾸준한 학습을 이어가세요!")
                .take(150)  // 최대 150자로

            // tags 파싱
            val tags = parseTags(json)

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
                    comment = "분석 결과를 불러올 수 없습니다",
                ),
                tags = emptySet()
            )
        }
    }

    /** JSON에서 tags 배열 파싱 */
    private fun parseTags(json: JSONObject): Set<LearningTag> {
        return try {
            val tagsArray = json.optJSONArray("tags") ?: return emptySet()

            val tagList = mutableListOf<String>()
            for (i in 0 until tagsArray.length()) {
                tagList.add(tagsArray.getString(i))
            }

            LearningTag.fromStringList(tagList)
        } catch (e: Exception) {
            Timber.e(e, "태그 파싱 실패")
            emptySet()
        }
    }

    /** 월간 회고 프롬프트 생성 */
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
            .sortedByDescending { it.value }
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

        // 평균 점수
        val avgScore = commits
            .mapNotNull { it.analysis?.moodScore }
            .average()
            .takeIf { !it.isNaN() }
            ?.toInt()
            ?: 50

        // 주요 태그
        val topTags = commits
            .flatMap { it.tags }
            .groupingBy { it.value }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(5)
            .joinToString(", ") { "${it.key}(${it.value}회)" }
            .ifEmpty { "없음" }

        // 특이사항
        val notablePatterns = buildList {
            add("평균 학습 점수: $avgScore/100")
            add("주요 학습 태그: $topTags")

            val confusedCount = commits.count { it.analysis?.mood == AIMood.CONFUSED }
            if (confusedCount > totalCommitCount / 4) {
                add("혼란스러움 상태가 자주 감지됨 (${confusedCount}회)")
            }

            val productiveCount = commits.count { it.analysis?.mood == AIMood.PRODUCTIVE }
            if (productiveCount > totalCommitCount / 3) {
                add("성과적인 학습이 많았음 (${productiveCount}회)")
            }
        }.joinToString("; ")

        return """
You are an AI coach helping a developer write a monthly retrospective based on their learning logs.

Write a monthly learning retrospective in KOREAN based on the following statistics.

[Instructions]
1. Focus on learning flow and pattern analysis rather than emotional comfort.
2. Explain based on data without exaggerating results.
3. Divide into the following 3 sections:
   - 한 달 요약 (Monthly Summary): 3-4 sentences
   - 학습 패턴 분석 (Pattern Analysis): 3-4 sentences
   - 다음 달을 위한 제안 (Suggestions for Next Month): 2-3 sentences

[Tone & Style]
- Objective and calm tone.
- Retrospective report style (not a diary).
- Keep it concise.

[Data]
- Period: $year-$month
- Total Commits: $totalCommitCount
- AI Mood Distribution: $moodDistribution
- Commits per Week: $weeklyCommitCount
- Notable Patterns: $notablePatterns
""".trimIndent()
    }
}
