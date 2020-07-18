package com.gscanlon21.reversedictionary.db

import androidx.room.TypeConverter
import com.gscanlon21.reversedictionary.repository.data.ApiType
import java.time.Instant

object TypeConverter {
    @TypeConverter
    @JvmStatic
    fun fromInstant(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    @JvmStatic
    fun toApiType(type: ApiType?): String? {
        return type?.name
    }

    @TypeConverter
    @JvmStatic
    fun fromApiType(value: String?): ApiType? {
        return value?.let { ApiType.valueOf(value) }
    }

    @TypeConverter
    @JvmStatic
    fun toInstant(time: Instant?): Long? {
        return time?.toEpochMilli()
    }
}
