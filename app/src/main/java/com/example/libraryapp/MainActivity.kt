package com.example.libraryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.libraryapp.data.AppDatabase
import com.example.libraryapp.data.LibraryRepository
import com.example.libraryapp.ui.AppNavigation
import com.example.libraryapp.ui.LibraryViewModel
import com.example.libraryapp.ui.theme.LibraryAppTheme

class MainActivity : ComponentActivity() {

    private val viewModel: LibraryViewModel by viewModels {
        val dao = AppDatabase.getInstance(applicationContext).libraryDao()
        LibraryViewModel.Factory(LibraryRepository(dao))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LibraryAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}
