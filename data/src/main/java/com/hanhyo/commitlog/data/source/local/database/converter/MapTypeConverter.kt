package com.hanhyo.commitlog.data.source.local.database.converter

import androidx.room.TypeConverter
import org.json.JSONObject

class MapTypeConverter {
    @TypeConverter
    fun fromIntIntMap(map: Map<Int, Int>?): String? {
        if (map == null) return null
        val json = JSONObject()
        map.forEach { (key, value) -> json.put(key.toString(), value) }
        return json.toString()
    }

    @TypeConverter
    fun toIntIntMap(jsonString: String?): Map<Int, Int>? {
        if (jsonString.isNullOrEmpty()) return null
        return try {
            val map = mutableMapOf<Int, Int>()
            val json = JSONObject(jsonString)
            json.keys().forEach { key ->
                map[key.toInt()] = json.getInt(key)
            }
            map
        } catch (e: Exception) {
            emptyMap()
        }
    }

    @TypeConverter
    fun fromStringIntMap(map: Map<String, Int>?): String? {
        if (map == null) return null
        val json = JSONObject()
        map.forEach { (key, value) -> json.put(key, value) }
        return json.toString()
    }

    @TypeConverter
    fun toStringIntMap(jsonString: String?): Map<String, Int>? {
        if (jsonString.isNullOrEmpty()) return null
        return try {
            val map = mutableMapOf<String, Int>()
            val json = JSONObject(jsonString)
            json.keys().forEach { key ->
                map[key] = json.getInt(key)
            }
            map
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
