package com.hanhyo.commitlog.data.worker

/**
 * WorkManager 관련 공통 상수
 */
object WorkerConstants {
    // Tags
    const val TAG_COMMIT_ANALYSIS = "commit_analysis"
    const val TAG_MONTHLY_REVIEW_MANUAL = "monthly_review_manual"
    const val TAG_MONTHLY_REVIEW_REGULAR = "monthly_review_regular"

    // Work Names
    const val WORK_NAME_MONTHLY_REVIEW_MANUAL_PREFIX = "monthly_review"
    const val WORK_NAME_MONTHLY_REVIEW_REGULAR = "MonthlyReviewRegularWork"

    // Keys
    const val KEY_COMMIT_ID = "commit_id"
    const val KEY_YEAR = "year"
    const val KEY_MONTH = "month"
    const val KEY_ERROR_MESSAGE = "error_message"
}
