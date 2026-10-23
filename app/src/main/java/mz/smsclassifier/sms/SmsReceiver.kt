package mz.smsclassifier.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.launch
import mz.smsclassifier.BuildConfig
import mz.smsclassifier.SmsClassifierApp
import mz.smsclassifier.data.PersistSmsWorker

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val sms = SmsParser.fromReceivedIntent(intent) ?: return
        val app = context.applicationContext as SmsClassifierApp
        val pending = goAsync()
        app.applicationScope.launch {
            try {
                val incoming = sms.copy(unread = !VisibleConversation.isOpen(sms.sender))
                app.smsRepository.addIncoming(incoming)
                PersistSmsWorker.enqueue(app)
                IncomingSmsNotifier.show(app, sms)
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "SMS received")
                }
            } catch (_: Exception) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "SMS persist failed")
                }
            } finally {
                pending.finish()
            }
        }
    }

    companion object {
        private const val TAG = "SmsReceiver"
    }
}
