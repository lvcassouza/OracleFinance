package com.oraclefinance.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.oraclefinance.app.di.appModule
import com.oraclefinance.app.di.networkModule
import com.oraclefinance.app.data.repository.AuthRepository
import com.oraclefinance.app.ui.screens.HomeScreen
import com.oraclefinance.app.ui.screens.LoginScreen
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKoin {
            androidContext(this@MainActivity)
            modules(listOf(networkModule, appModule))
        }
        val startDestination = run {
            val auth = getKoin().get<AuthRepository>()
            if (auth.isLoggedIn()) "home" else "login"
        }
        setContent { App(startDestination) }
    }
}

@Composable
fun App(startDestination: String) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") { LoginScreen(navController) }
        composable("home") { HomeScreen() }
    }
}
