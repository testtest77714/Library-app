package com.example.libraryapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.libraryapp.data.Library
import com.example.libraryapp.ui.LibraryViewModel
import com.example.libraryapp.ui.theme.MementoCardBlue
import com.example.libraryapp.ui.theme.MementoTeal

// A small rotating palette so library icons aren't all identical,
// echoing the colored folder icons seen in the real app.
private val libraryIconColors = listOf(
    MementoCardBlue,
    Color(0xFF43A047),
    Color(0xFFFB8C00),
    Color(0xFF8E24AA),
    Color(0xFFE53935)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryListScreen(
    viewModel: LibraryViewModel,
    onOpenLibrary: (Long) -> Unit
) {
    val libraries by viewModel.libraries.collectAsState(initial = emptyList())
    var showCreateDialog by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }
    var showSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filtered = if (searchQuery.isBlank()) libraries
    else libraries.filter { it.name.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (showSearch) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search libraries") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text("My libraries")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { /* workspace drawer — not implemented in this MVP */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        showSearch = !showSearch
                        if (!showSearch) searchQuery = ""
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                    Box {
                        IconButton(onClick = { showOverflowMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                        DropdownMenu(expanded = showOverflowMenu, onDismissRequest = { showOverflowMenu = false }) {
                            DropdownMenuItem(text = { Text("Add Library") }, onClick = {
                                showOverflowMenu = false
                                showCreateDialog = true
                            })
                            DropdownMenuItem(text = { Text("Add Group") }, enabled = false, onClick = {})
                            DropdownMenuItem(text = { Text("Add Dashboard") }, enabled = false, onClick = {})
                            DropdownMenuItem(text = { Text("Edit group") }, enabled = false, onClick = {})
                            DropdownMenuItem(text = { Text("Open by URL") }, enabled = false, onClick = {})
                            DropdownMenuItem(text = { Text("View") }, enabled = false, onClick = {})
                            DropdownMenuItem(text = { Text("Shortcuts") }, enabled = false, onClick = {})
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MementoTeal,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "New library", tint = Color.White)
            }
        }
    ) { padding ->
        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        if (libraries.isEmpty()) "No libraries yet" else "No libraries match \"$searchQuery\"",
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (libraries.isEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Tap + to create your first library",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(filtered, key = { it.id }) { library ->
                    LibraryRow(
                        library = library,
                        colorIndex = (library.id % libraryIconColors.size).toInt(),
                        onClick = { onOpenLibrary(library.id) },
                        onDelete = { viewModel.deleteLibrary(library) }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateLibraryDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name ->
                showCreateDialog = false
                viewModel.createLibrary(name) { newId -> onOpenLibrary(newId) }
            }
        )
    }
}

@Composable
private fun LibraryRow(
    library: Library,
    colorIndex: Int,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = libraryIconColors[colorIndex],
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    Icons.Outlined.Description,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(library.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                "Library",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Library options")
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(text = { Text("Protection") }, enabled = false, onClick = {})
                DropdownMenuItem(text = { Text("Upload to Cloud") }, enabled = false, onClick = {})
                DropdownMenuItem(text = { Text("Link to Google Sheets") }, enabled = false, onClick = {})
                DropdownMenuItem(text = { Text("Create Shortcut") }, enabled = false, onClick = {})
                DropdownMenuItem(text = { Text("Copy") }, enabled = false, onClick = {})
                DropdownMenuItem(text = { Text("Edit") }, onClick = { showMenu = false; onClick() })
                DropdownMenuItem(text = { Text("Scripts") }, enabled = false, onClick = {})
                DropdownMenuItem(text = { Text("Reset view") }, enabled = false, onClick = {})
                DropdownMenuItem(text = { Text("Delete") }, onClick = { showMenu = false; onDelete() })
                DropdownMenuItem(text = { Text("Info") }, enabled = false, onClick = {})
            }
        }
    }
}

@Composable
private fun CreateLibraryDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New library") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Library name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onCreate(name.trim()) },
                enabled = name.isNotBlank()
            ) { Text("Create") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
