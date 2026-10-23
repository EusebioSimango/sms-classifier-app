package mz.smsclassifier

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mz.smsclassifier.settings.ThemeMode
import mz.smsclassifier.sms.OPEN_SENDER_EXTRA
import mz.smsclassifier.sms.VisibleConversation
import mz.smsclassifier.ui.InboxRoute
import mz.smsclassifier.ui.theme.SmsClassifierTheme

class MainActivity : ComponentActivity() {
    private var openSender by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE,
        )
        enableEdgeToEdge()
        openSender = intent.getStringExtra(OPEN_SENDER_EXTRA)
        val themePreferences = (application as SmsClassifierApp).themePreferences
        setContent {
            val themeMode by themePreferences.themeMode.collectAsStateWithLifecycle(
                initialValue = ThemeMode.SYSTEM,
            )
            SmsClassifierTheme(themeMode = themeMode) {
                InboxRoute(
                    openSender = openSender,
                    onOpenSenderConsumed = { openSender = null },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        openSender = intent.getStringExtra(OPEN_SENDER_EXTRA)
    }

    override fun onStart() {
        super.onStart()
        VisibleConversation.resumed = true
    }

    override fun onStop() {
        VisibleConversation.resumed = false
        super.onStop()
    }
}
