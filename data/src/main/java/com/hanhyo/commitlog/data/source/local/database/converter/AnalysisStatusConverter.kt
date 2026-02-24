package com.hanhyo.commitlog.data.source.local.database.converter

import androidx.room.TypeConverter
import com.hanhyo.commitlog.domain.model.AnalysisStatus

class AnalysisStatusConverter {
    @TypeConverter
    fun fromAnalysisStatus(status: AnalysisStatus): String {
        return status.name
    }

    @TypeConverter
    fun toAnalysisStatus(statusString: String): AnalysisStatus {
        return try {
            AnalysisStatus.valueOf(statusString)
        } catch (e: Exception) {
            AnalysisStatus.NONE
        }
    }
}
