package gr.aueb.thriveon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import gr.aueb.thriveon.ui.navigation.AppNavHost
import gr.aueb.thriveon.ui.theme.ThriveOnTheme
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI

class MainActivity : ComponentActivity() {
    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            KoinAndroidContext {
                ThriveOnTheme {
                    AppNavHost(
                        navController = rememberNavController()
                    )
                }
            }
        }
    }
}
