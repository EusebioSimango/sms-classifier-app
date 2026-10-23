package mz.smsclassifier.sms

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import mz.smsclassifier.SmsClassifierApp

internal const val REPLY_TEXT_KEY = "mz.smsclassifier.REPLY_TEXT"

class NotificationReplyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val text = RemoteInput.getResultsFromIntent(intent)
            ?.getCharSequence(REPLY_TEXT_KEY)
            ?.toString()
            ?.trim()
            .orEmpty()
        val sender = intent.getStringExtra(OPEN_SENDER_EXTRA) ?: return
        if (text.isEmpty() || !isReplyable(sender)) return
        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) ==
            PackageManager.PERMISSION_GRANTED
        if (!granted) return
        val pending = goAsync()
        val app = context.applicationContext as SmsClassifierApp
        app.applicationScope.launch {
            try {
                val lines = SimDirectory(app).lines()
                val subscriptionId = SimChoice.resolve(app, lines)
                val sent = runCatching { app.smsSender.send(sender, text, subscriptionId) }.getOrDefault(false)
                if (sent) {
                    app.smsRepository.addOutgoing(
                        IncomingSms(
                            sender = sender,
                            body = text,
                            receivedAtMillis = System.currentTimeMillis(),
                            outgoing = true,
                        ),
                    )
                    IncomingSmsNotifier.cancel(app, sender)
                }
            } finally {
                pending.finish()
            }
        }
    }
}
