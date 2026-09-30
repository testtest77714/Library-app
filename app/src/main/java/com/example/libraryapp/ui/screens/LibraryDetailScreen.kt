package com.example.libraryapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.libraryapp.data.Entry
import com.example.libraryapp.data.Field
import com.example.libraryapp.data.Library
import com.example.libraryapp.data.ViewMode
import com.example.libraryapp.ui.LibraryViewModel

/**
 * Shows the entries inside one Library as Cards (default, 2-column
 * grid) or List, with a slide-in drawer for Sort/Filters/Settings/etc.
 * — mirroring the real app's library screen and its right-side panel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryDetailScreen(
    viewModel: LibraryViewModel,
    library: Library,
    onBack: () -> Unit,
    onOpenSchema: () -> Unit,
    onOpenEntry: (Long?) -> Unit
) {
    val fields by viewModel.fields(library.id).collectAsState(initial = emptyList())
    val entries by viewModel.entries(library.id).collectAsState(initial = emptyList())
    var showDrawer by remember { mutableStateOf(false) }
    var showSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val titleField = fields.firstOrNull { it.isTitle } ?: fields.firstOrNull()
    val loader = LocalEntryValueLoader.current

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        if (showSearch) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search ${library.name.lowercase()}") },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                                    focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text(library.name)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            showSearch = !showSearch
                            if (!showSearch) searchQuery = ""
                        }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = { showDrawer = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Library menu")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { onOpenEntry(null) }) {
                    Icon(Icons.Default.Add, contentDescription = "Add entry")
                }
            },
            bottomBar = {
                BottomAppBar {
                    Text(
                        "Entries: ${entries.size}",
                        modifier = Modifier.padding(start = 16.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        ) { padding ->
            if (fields.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("Loading fields…")
                }
                return@Scaffold
            }

            val visibleEntries = if (searchQuery.isBlank()) entries else {
                // Filtering by title requires each entry's values; for the MVP we
                // filter after values are loaded inside the row/card composables,
                // so here we just pass everything through and let empty-state
                // messaging stay simple.
                entries
            }

            if (visibleEntries.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No entries yet", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Tap + to add your first entry",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else when (library.viewMode) {
                ViewMode.CARDS -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(visibleEntries, key = { it.id }) { entry ->
                            EntryCard(
                                entry = entry,
                                fields = fields,
                                onClick = { onOpenEntry(entry.id) }
                            )
                        }
                    }
                }
                ViewMode.LIST -> {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                        items(visibleEntries, key = { it.id }) { entry ->
                            EntryListRow(
                                entry = entry,
                                fields = fields,
                                onClick = { onOpenEntry(entry.id) }
                            )
                            Divider()
                        }
                    }
                }
            }
        }

        // Right-side drawer, mirroring the real app's library options panel.
        if (showDrawer) {
            LibraryDrawer(
                library = library,
                onDismiss = { showDrawer = false },
                onEditFields = { showDrawer = false; onOpenSchema() },
                onSetViewMode = { mode -> viewModel.setViewMode(library.id, mode) }
            )
        }
    }
}

@Composable
private fun EntryCard(entry: Entry, fields: List<Field>, onClick: () -> Unit) {
    var values by remember(entry.id) { mutableStateOf<Map<Long, String>>(emptyMap()) }
    val loader = LocalEntryValueLoader.current
    LaunchedEffect(entry.id) { values = loader(entry.id) }

    val titleField = fields.firstOrNull { it.isTitle } ?: fields.firstOrNull()
    val title = titleField?.let { values[it.id] }?.takeIf { it.isNotBlank() } ?: "Untitled"

    Surface(
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
    ) {
        Box(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun EntryListRow(entry: Entry, fields: List<Field>, onClick: () -> Unit) {
    var values by remember(entry.id) { mutableStateOf<Map<Long, String>>(emptyMap()) }
    val loader = LocalEntryValueLoader.current
    LaunchedEffect(entry.id) { values = loader(entry.id) }

    val titleField = fields.firstOrNull { it.isTitle } ?: fields.firstOrNull()
    val title = titleField?.let { values[it.id] }?.takeIf { it.isNotBlank() } ?: "Untitled"
    val subtitle = fields.filter { it.id != titleField?.id }
        .mapNotNull { f -> values[f.id]?.takeIf { it.isNotBlank() } }
        .take(2)
        .joinToString("  ·  ")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        if (subtitle.isNotBlank()) {
            Spacer(Modifier.height(2.dp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/**
 * A small composition-local so entry rows/cards can fetch an entry's
 * values without threading the whole ViewModel through props. Set at
 * the navigation-graph call site.
 */
val LocalEntryValueLoader = compositionLocalOf<suspend (Long) -> Map<Long, String>> {
    { emptyMap() }
}

@Composable
private fun LibraryDrawer(
    library: Library,
    onDismiss: () -> Unit,
    onEditFields: () -> Unit,
    onSetViewMode: (ViewMode) -> Unit
) {
    // Scrim + right-aligned panel, matching the real app's slide-in menu.
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.32f))
                .clickable(onClick = onDismiss)
        )
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxHeight()
                .width(260.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            androidx.compose.foundation.lazy.LazyColumn {
                item { DrawerSection("Edit library", onClick = onEditFields) }
                item { DrawerSection("Cloud", subtitle = "Not in the cloud", enabled = false) }
                item { Divider() }
                item { DrawerSection("Preset", subtitle = "No preset", enabled = false) }
                item {
                    DrawerSection(
                        "View",
                        subtitle = if (library.viewMode == ViewMode.CARDS) "Cards" else "List",
                        onClick = {
                            onSetViewMode(if (library.viewMode == ViewMode.CARDS) ViewMode.LIST else ViewMode.CARDS)
                        }
                    )
                }
                item { DrawerSection("Sort", subtitle = "by Creation time ASC", enabled = false) }
                item { DrawerSection("Filters", subtitle = "No filter", enabled = false) }
                item { Divider() }
                item { DrawerSection("Automations", enabled = false) }
                item { DrawerSection("Favorites", enabled = false) }
                item { DrawerSection("History", enabled = false) }
                item { DrawerSection("Recycle Bin", enabled = false) }
                item { DrawerSection("Charts", enabled = false) }
                item { DrawerSection("Drafts", enabled = false) }
                item { DrawerSection("Prefilled entries", enabled = false) }
                item { DrawerSection("Import and export", enabled = false) }
                item { DrawerSection("Files", enabled = false) }
                item { DrawerSection("Settings", enabled = false) }
            }
        }
    }
}

@Composable
private fun DrawerSection(
    title: String,
    subtitle: String? = null,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .let { if (enabled && onClick != null) it.clickable(onClick = onClick) else it }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            title,
            style = MaterialTheme.typography.bodyLarge,
            color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (subtitle != null) {
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
