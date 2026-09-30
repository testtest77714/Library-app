package com.example.libraryapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.libraryapp.data.Entry
import com.example.libraryapp.ui.screens.EntryFormScreen
import com.example.libraryapp.ui.screens.LibraryDetailScreen
import com.example.libraryapp.ui.screens.LibraryListScreen
import com.example.libraryapp.ui.screens.LocalEntryValueLoader
import com.example.libraryapp.ui.screens.SchemaEditorScreen
import androidx.compose.runtime.CompositionLocalProvider

private object Routes {
    const val LIST = "list"
    const val LIBRARY = "library/{libraryId}/{libraryName}"
    const val SCHEMA = "schema/{libraryId}"
    const val ENTRY = "entry/{libraryId}/{entryId}"

    fun library(id: Long, name: String) = "library/$id/${java.net.URLEncoder.encode(name, "UTF-8")}"
    fun schema(id: Long) = "schema/$id"
    fun entry(libraryId: Long, entryId: Long?) = "entry/$libraryId/${entryId ?: -1}"
}

@Composable
fun AppNavigation(viewModel: LibraryViewModel) {
    val navController = rememberNavController()

    // Provide the entry-value loader used by list rows to fetch values lazily.
    CompositionLocalProvider(LocalEntryValueLoader provides { entryId -> viewModel.valuesForEntry(entryId) }) {
        NavHost(navController = navController, startDestination = Routes.LIST) {

            composable(Routes.LIST) {
                LibraryListScreen(
                    viewModel = viewModel,
                    onOpenLibrary = { id ->
                        navController.navigate(Routes.library(id, "Library"))
                    }
                )
            }

            composable(
                route = Routes.LIBRARY,
                arguments = listOf(
                    navArgument("libraryId") { type = NavType.LongType },
                    navArgument("libraryName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val libraryId = backStackEntry.arguments?.getLong("libraryId") ?: return@composable
                val libraries by viewModel.libraries.collectAsState(initial = emptyList())
                val library = libraries.firstOrNull { it.id == libraryId }

                if (library != null) {
                    LibraryDetailScreen(
                        viewModel = viewModel,
                        library = library,
                        onBack = { navController.popBackStack() },
                        onOpenSchema = { navController.navigate(Routes.schema(libraryId)) },
                        onOpenEntry = { entryId -> navController.navigate(Routes.entry(libraryId, entryId)) }
                    )
                }
            }

            composable(
                route = Routes.SCHEMA,
                arguments = listOf(navArgument("libraryId") { type = NavType.LongType })
            ) { backStackEntry ->
                val libraryId = backStackEntry.arguments?.getLong("libraryId") ?: return@composable
                SchemaEditorScreen(
                    viewModel = viewModel,
                    libraryId = libraryId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.ENTRY,
                arguments = listOf(
                    navArgument("libraryId") { type = NavType.LongType },
                    navArgument("entryId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val libraryId = backStackEntry.arguments?.getLong("libraryId") ?: return@composable
                val rawEntryId = backStackEntry.arguments?.getLong("entryId") ?: -1L
                val entryId = if (rawEntryId == -1L) null else rawEntryId

                EntryFormScreen(
                    viewModel = viewModel,
                    libraryId = libraryId,
                    entryId = entryId,
                    onDone = { navController.popBackStack() },
                    onDelete = if (entryId != null) {
                        {
                            viewModel.deleteEntry(Entry(id = entryId, libraryId = libraryId))
                            navController.popBackStack()
                        }
                    } else null
                )
            }
        }
    }
}
