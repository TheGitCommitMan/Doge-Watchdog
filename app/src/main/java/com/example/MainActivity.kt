package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.TransactionRepository
import com.example.data.USAspendingApi
import com.example.ui.WatchdogApp
import com.example.ui.WatchdogViewModel
import com.example.ui.WatchdogViewModelFactory
import com.example.ui.theme.DOGEWatchdogTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Core dependency initializations
        val database = AppDatabase.getDatabase(this)
        val dao = database.transactionDao()
        val api = USAspendingApi.create()
        val repository = TransactionRepository(dao, api)

        val factory = WatchdogViewModelFactory(application, repository)
        val viewModel = ViewModelProvider(this, factory)[WatchdogViewModel::class.java]

        setContent {
            DOGEWatchdogTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    WatchdogApp(viewModel = viewModel)
                }
            }
        }
    }
}
