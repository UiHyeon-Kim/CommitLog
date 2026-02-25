package com.hanhyo.commitlog.presentation.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * CommitLogWidget의 상태 변경 및 생명주기 이벤트를 수신하는 리시버.
 */
class CommitLogWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = CommitLogWidget()
}
