package com.example.libraryapp.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromFieldType(type: FieldType): String = type.name

    @TypeConverter
    fun toFieldType(value: String): FieldType = FieldType.valueOf(value)

    @TypeConverter
    fun fromViewMode(mode: ViewMode): String = mode.name

    @TypeConverter
    fun toViewMode(value: String): ViewMode = ViewMode.valueOf(value)
}
