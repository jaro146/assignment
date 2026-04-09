package com.example.o2assignment

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.o2assignment.network.di.util.NetworkMonitor
import com.example.o2assignment.theme.AppTheme
import org.koin.android.ext.android.get
import org.koin.compose.KoinContext

class MainActivity : AppCompatActivity() {

    val networkMonitor: NetworkMonitor = get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val appState = rememberAppState(
                networkMonitor = networkMonitor,
            )

            AppTheme {
                O2AssignmentApp(appState)
            }
        }
    }
}
