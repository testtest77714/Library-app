package com.example.libraryapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** How a library's entries are displayed — mirrors Memento's View setting. */
enum class ViewMode {
    CARDS,
    LIST
}

/**
 * A Library is a user-defined collection — like a database table.
 * Each Library owns its own set of Fields (schema) and Entries (rows).
 */
@Entity(tableName = "libraries")
data class Library(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val viewMode: ViewMode = ViewMode.CARDS
)
