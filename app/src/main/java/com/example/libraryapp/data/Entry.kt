package com.example.libraryapp.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * An Entry is one record inside a Library. Its actual field values
 * live in the separate EntryValue table (one row per field), which
 * keeps the schema flexible — adding a field never requires a
 * migration of existing entries.
 */
@Entity(
    tableName = "entries",
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
data class Entry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val libraryId: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * One field's value on one entry. Value is stored as text; the UI
 * parses/formats it according to the field's declared FieldType.
 */
@Entity(
    tableName = "entry_values",
    primaryKeys = ["entryId", "fieldId"],
    foreignKeys = [
        ForeignKey(
            entity = Entry::class,
            parentColumns = ["id"],
            childColumns = ["entryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Field::class,
            parentColumns = ["id"],
            childColumns = ["fieldId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("fieldId")]
)
data class EntryValue(
    val entryId: Long,
    val fieldId: Long,
    val value: String
)

/** An Entry bundled with its values, keyed by fieldId, for convenient use in the UI. */
data class EntryWithValues(
    val entry: Entry,
    val values: Map<Long, String>
)
