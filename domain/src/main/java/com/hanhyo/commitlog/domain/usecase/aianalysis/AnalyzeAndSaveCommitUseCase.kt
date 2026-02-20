package com.hanhyo.commitlog.domain.usecase.aianalysis

import com.hanhyo.commitlog.domain.common.runSuspendCatching
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.CommitId
import com.hanhyo.commitlog.domain.repository.AiAnalysisRepository
import com.hanhyo.commitlog.domain.repository.CommitRepository
import javax.inject.Inject

class AnalyzeAndSaveCommitUseCase @Inject constructor(
    private val aiRepository: AiAnalysisRepository,
    private val commitRepository: CommitRepository
) {
    suspend operator fun invoke(commit: Commit): Result<Commit> = runSuspendCatching {
        // AI 분석
        val analysisResult = aiRepository.analyzeCommit(
            title = commit.title,
            learnedToday = commit.learnedToday,
            difficulties = commit.difficulties,
            tomorrowPlan = commit.tomorrowPlan,
        )

        // 분석 결과 삽입
        val analyzedCommit = commit.withAnalysis(
            analysis = analysisResult.analysis,
            tags = analysisResult.tags
        )

        val id = commitRepository.saveCommit(analyzedCommit)
        analyzedCommit.copy(id = CommitId(id))
    }
}
