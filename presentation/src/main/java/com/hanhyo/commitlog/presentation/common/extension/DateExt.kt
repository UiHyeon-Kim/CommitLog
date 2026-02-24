package com.hanhyo.commitlog.presentation.common.extension

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.time.Instant
import java.time.ZoneId
import java.util.Locale

/** Long (Timestamp)를 한국 오전/오후 시간 형식으로 변환 */
fun Long.toKoreanTimeFormat(): String {
    val formatter = DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN)
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}

/** LocalDate를 한국어 날짜 형식으로 변환 */
fun LocalDate.toKoreanFormat(): String = "${year}년 ${monthValue}월 ${dayOfMonth}일"

/** LocalDate를 요일 포함 날짜 형식으로 변환 */
fun LocalDate.toKoreanFormatWithDayOfWeek(): String {
    val dayOfWeek = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN)

    return "${year}년 ${monthValue}월 ${dayOfMonth}일 $dayOfWeek"
}

/** LocalDate를 상대적 시간으로 변환 - 오늘, 어제 */
fun LocalDate.toRelativeString(): String {
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)

    return when {
        this == today -> "오늘"
        this == yesterday -> "어제"
        this.year == today.year -> "${monthValue}월 ${dayOfMonth}일"
        else -> "${year}년 ${monthValue}월 ${dayOfMonth}일"
    }
}

/** 두 날짜 사이 일수 계산 */
fun LocalDate.dayBetween(other: LocalDate): Long = ChronoUnit.DAYS.between(this, other)

/** ISO 형식 yyyy-MM-dd */
fun LocalDate.toIsoString(): String = format(DateTimeFormatter.ISO_LOCAL_DATE)
