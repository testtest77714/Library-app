package com.example.libraryapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.libraryapp.data.Field
import com.example.libraryapp.data.FieldType
import com.example.libraryapp.ui.LibraryViewModel

/**
 * Renders a form generated from the library's schema, for creating a
 * new entry (entryId == null) or editing an existing one.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryFormScreen(
    viewModel: LibraryViewModel,
    libraryId: Long,
    entryId: Long?,
    onDone: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val fields by viewModel.fields(libraryId).collectAsState(initial = emptyList())
    var values by remember { mutableStateOf<Map<Long, String>>(emptyMap()) }
    var errors by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var loaded by remember { mutableStateOf(entryId == null) }

    LaunchedEffect(entryId) {
        if (entryId != null) {
            values = viewModel.valuesForEntry(entryId)
            loaded = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (entryId == null) "New entry" else "Edit entry") },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel")
                    }
                },
                actions = {
                    if (entryId != null && onDelete != null) {
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete entry")
                        }
                    }
                    IconButton(onClick = {
                        val missing = fields.filter { it.required && values[it.id].isNullOrBlank() }
                            .map { it.id }
                            .toSet()
                        if (missing.isNotEmpty()) {
                            errors = missing
                        } else {
                            viewModel.saveEntry(libraryId, entryId, values) { onDone() }
                        }
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            )
        }
    ) { padding ->
        if (!loaded) {
            Box(Modifier.fillMaxSize().padding(padding)) { }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            fields.forEach { field ->
                FieldInput(
                    field = field,
                    value = values[field.id] ?: "",
                    isError = errors.contains(field.id),
                    onValueChange = { newValue ->
                        values = values.toMutableMap().apply { put(field.id, newValue) }
                        errors = errors - field.id
                    }
                )
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun FieldInput(
    field: Field,
    value: String,
    isError: Boolean,
    onValueChange: (String) -> Unit
) {
    when (field.type) {
        FieldType.CHECKBOX -> {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Checkbox(
                    checked = value == "true",
                    onCheckedChange = { onValueChange(if (it) "true" else "false") }
                )
                Spacer(Modifier.width(8.dp))
                Text(field.name + if (field.required) " *" else "")
            }
        }
        FieldType.NUMBER -> {
            OutlinedTextField(
                value = value,
                onValueChange = { input -> if (input.all { it.isDigit() || it == '.' || it == '-' }) onValueChange(input) },
                label = { Text(field.name + if (field.required) " *" else "") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = isError,
                supportingText = { if (isError) Text("Required") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        FieldType.DATE -> {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(field.name + if (field.required) " *" else "") },
                placeholder = { Text("YYYY-MM-DD") },
                isError = isError,
                supportingText = { if (isError) Text("Required") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        FieldType.TEXT -> {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(field.name + if (field.required) " *" else "") },
                isError = isError,
                supportingText = { if (isError) Text("Required") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
