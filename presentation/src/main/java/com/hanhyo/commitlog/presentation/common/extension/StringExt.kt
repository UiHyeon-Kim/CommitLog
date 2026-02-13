package com.hanhyo.commitlog.presentation.common.extension

/** 문자열이 비어있지 않으면 변환 */
fun String?.ifNotBlankOrNull(transform: (String) -> String): String? {
    return if (this.isNullOrBlank()) {
        null
    } else {
        transform(this)
    }
}

/** 텍스트 길이 제한 */
fun String.limitLength(maxLength: Int): String {
    return if (length > maxLength) {
        substring(0, maxLength) + "..."
    } else {
        this
    }
}
