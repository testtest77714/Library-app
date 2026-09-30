package com.example.libraryapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {

    // ---------- Libraries ----------

    @Query("SELECT * FROM libraries ORDER BY createdAt DESC")
    fun getLibraries(): Flow<List<Library>>

    @Query("SELECT * FROM libraries WHERE id = :libraryId")
    fun getLibrary(libraryId: Long): Flow<Library?>

    @Insert
    suspend fun insertLibrary(library: Library): Long

    @Delete
    suspend fun deleteLibrary(library: Library)

    @Update
    suspend fun updateLibrary(library: Library)

    @Query("UPDATE libraries SET viewMode = :mode WHERE id = :libraryId")
    suspend fun setViewMode(libraryId: Long, mode: ViewMode)

    // ---------- Fields ----------

    @Query("SELECT * FROM fields WHERE libraryId = :libraryId ORDER BY position ASC, id ASC")
    fun getFields(libraryId: Long): Flow<List<Field>>

    @Insert
    suspend fun insertField(field: Field): Long

    @Update
    suspend fun updateField(field: Field)

    @Delete
    suspend fun deleteField(field: Field)

    /** Clears title flag from all fields in a library before setting a new one. */
    @Query("UPDATE fields SET isTitle = 0 WHERE libraryId = :libraryId")
    suspend fun clearTitleFlags(libraryId: Long)

    @Transaction
    suspend fun setTitleField(libraryId: Long, fieldId: Long) {
        clearTitleFlags(libraryId)
        updateField(getFieldOnce(fieldId).copy(isTitle = true))
    }

    @Query("SELECT * FROM fields WHERE id = :fieldId")
    suspend fun getFieldOnce(fieldId: Long): Field

    /** Persists a new field order after a drag-and-drop reorder in the schema editor. */
    @Transaction
    suspend fun reorderFields(orderedFieldIds: List<Long>) {
        orderedFieldIds.forEachIndexed { index, fieldId ->
            updateField(getFieldOnce(fieldId).copy(position = index))
        }
    }

    // ---------- Entries ----------

    @Query("SELECT * FROM entries WHERE libraryId = :libraryId ORDER BY createdAt DESC")
    fun getEntries(libraryId: Long): Flow<List<Entry>>

    @Query("SELECT * FROM entries WHERE id = :entryId")
    suspend fun getEntryOnce(entryId: Long): Entry

    @Insert
    suspend fun insertEntry(entry: Entry): Long

    @Update
    suspend fun updateEntry(entry: Entry)

    @Delete
    suspend fun deleteEntry(entry: Entry)

    @Query("SELECT * FROM entry_values WHERE entryId = :entryId")
    fun getValuesForEntry(entryId: Long): Flow<List<EntryValue>>

    @Query("SELECT * FROM entry_values WHERE entryId = :entryId")
    suspend fun getValuesForEntryOnce(entryId: Long): List<EntryValue>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertValue(value: EntryValue)

    @Query("DELETE FROM entry_values WHERE entryId = :entryId")
    suspend fun clearValues(entryId: Long)

    /**
     * Saves an entry's full set of field values in one transaction:
     * creates the Entry row if needed, then replaces all its values.
     */
    @Transaction
    suspend fun saveEntry(libraryId: Long, entryId: Long?, values: Map<Long, String>): Long {
        val id = if (entryId == null) {
            insertEntry(Entry(libraryId = libraryId))
        } else {
            updateEntry(getEntryOnce(entryId).copy(updatedAt = System.currentTimeMillis()))
            entryId
        }
        clearValues(id)
        values.forEach { (fieldId, value) ->
            if (value.isNotBlank()) {
                upsertValue(EntryValue(entryId = id, fieldId = fieldId, value = value))
            }
        }
        return id
    }
}
