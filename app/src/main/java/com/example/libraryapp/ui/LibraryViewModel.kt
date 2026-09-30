package com.example.libraryapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.libraryapp.data.Entry
import com.example.libraryapp.data.Field
import com.example.libraryapp.data.FieldType
import com.example.libraryapp.data.Library
import com.example.libraryapp.data.LibraryRepository
import com.example.libraryapp.data.ViewMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class LibraryViewModel(private val repository: LibraryRepository) : ViewModel() {

    val libraries: Flow<List<Library>> = repository.libraries()

    fun fields(libraryId: Long): Flow<List<Field>> = repository.fields(libraryId)

    fun entries(libraryId: Long): Flow<List<Entry>> = repository.entries(libraryId)

    fun createLibrary(name: String, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.createLibrary(name)
            onCreated(id)
        }
    }

    fun deleteLibrary(library: Library) {
        viewModelScope.launch { repository.deleteLibrary(library) }
    }

    fun setViewMode(libraryId: Long, mode: ViewMode) {
        viewModelScope.launch { repository.setViewMode(libraryId, mode) }
    }

    fun reorderFields(orderedFieldIds: List<Long>) {
        viewModelScope.launch { repository.reorderFields(orderedFieldIds) }
    }

    fun addField(libraryId: Long, name: String, type: FieldType, position: Int) {
        viewModelScope.launch { repository.addField(libraryId, name, type, position) }
    }

    fun deleteField(field: Field) {
        viewModelScope.launch { repository.deleteField(field) }
    }

    fun setTitleField(libraryId: Long, fieldId: Long) {
        viewModelScope.launch { repository.setTitleField(libraryId, fieldId) }
    }

    suspend fun valuesForEntry(entryId: Long): Map<Long, String> =
        repository.valuesForEntryOnce(entryId)

    fun saveEntry(libraryId: Long, entryId: Long?, values: Map<Long, String>, onSaved: () -> Unit) {
        viewModelScope.launch {
            repository.saveEntry(libraryId, entryId, values)
            onSaved()
        }
    }

    fun deleteEntry(entry: Entry) {
        viewModelScope.launch { repository.deleteEntry(entry) }
    }

    class Factory(private val repository: LibraryRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LibraryViewModel(repository) as T
        }
    }
}
