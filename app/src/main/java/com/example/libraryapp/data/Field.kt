package com.example.libraryapp.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * The types of Field a library's schema can contain.
 * Kept as a small, stable set for the MVP — easy to extend later
 * (see the recreation spec's field registry concept).
 */
enum class FieldType {
    TEXT,
    NUMBER,
    DATE,
    CHECKBOX
}

/**
 * A Field defines one column of a Library's schema — its name, type,
 * whether it's required, and whether it's the "title" field used to
 * label entries in lists (Memento's "title provider" concept).
 */
@Entity(
    tableName = "fields",
    foreignKeys = [
        ForeignKey(
            entity = Library::class,
            parentColumns = ["id"],
            childColumns = ["libraryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("libraryId")]
)
data class Field(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val libraryId: Long,
    val name: String,
    val type: FieldType,
    val isTitle: Boolean = false,
    val required: Boolean = false,
    val position: Int = 0
)
