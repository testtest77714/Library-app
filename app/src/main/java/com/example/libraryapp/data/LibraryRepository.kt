package com.example.libraryapp.data

import kotlinx.coroutines.flow.Flow

/**
 * Thin repository over the DAO. Keeps the ViewModel free of Room
 * specifics and gives a single place to add caching or sync later.
 */
class LibraryRepository(private val dao: LibraryDao) {

    fun libraries(): Flow<List<Library>> = dao.getLibraries()

    fun library(id: Long): Flow<Library?> = dao.getLibrary(id)

    suspend fun createLibrary(name: String): Long {
        val libraryId = dao.insertLibrary(Library(name = name))
        // Every new library starts with one default text field, used as its title.
        dao.insertField(
            Field(
                libraryId = libraryId,
                name = "Name",
                type = FieldType.TEXT,
                isTitle = true,
                required = true,
                position = 0
            )
        )
        return libraryId
    }

    suspend fun deleteLibrary(library: Library) = dao.deleteLibrary(library)

    suspend fun setViewMode(libraryId: Long, mode: ViewMode) = dao.setViewMode(libraryId, mode)

    fun fields(libraryId: Long): Flow<List<Field>> = dao.getFields(libraryId)

    suspend fun addField(libraryId: Long, name: String, type: FieldType, position: Int) {
        dao.insertField(Field(libraryId = libraryId, name = name, type = type, position = position))
    }

    suspend fun deleteField(field: Field) = dao.deleteField(field)

    suspend fun reorderFields(orderedFieldIds: List<Long>) = dao.reorderFields(orderedFieldIds)

    suspend fun setTitleField(libraryId: Long, fieldId: Long) = dao.setTitleField(libraryId, fieldId)

    fun entries(libraryId: Long): Flow<List<Entry>> = dao.getEntries(libraryId)

    fun valuesForEntry(entryId: Long): Flow<List<EntryValue>> = dao.getValuesForEntry(entryId)

    suspend fun valuesForEntryOnce(entryId: Long): Map<Long, String> =
        dao.getValuesForEntryOnce(entryId).associate { it.fieldId to it.value }

    suspend fun saveEntry(libraryId: Long, entryId: Long?, values: Map<Long, String>): Long =
        dao.saveEntry(libraryId, entryId, values)

    suspend fun deleteEntry(entry: Entry) = dao.deleteEntry(entry)
}
