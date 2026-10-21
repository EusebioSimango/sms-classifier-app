package mz.feng.smsclassifier

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mz.feng.smsclassifier.settings.ThemeMode
import mz.feng.smsclassifier.ui.InboxRoute
import mz.feng.smsclassifier.ui.theme.SmsClassifierTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE,
        )
        enableEdgeToEdge()
        val themePreferences = (application as SmsClassifierApp).themePreferences
        setContent {
            val themeMode by themePreferences.themeMode.collectAsStateWithLifecycle(
                initialValue = ThemeMode.SYSTEM,
            )
            SmsClassifierTheme(themeMode = themeMode) {
                InboxRoute()
            }
        }
    }
}
