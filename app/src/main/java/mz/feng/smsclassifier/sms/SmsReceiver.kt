package mz.feng.smsclassifier.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.launch
import mz.feng.smsclassifier.BuildConfig
import mz.feng.smsclassifier.SmsClassifierApp
import mz.feng.smsclassifier.data.PersistSmsWorker

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val sms = SmsParser.fromReceivedIntent(intent) ?: return
        val app = context.applicationContext as SmsClassifierApp
        val pending = goAsync()
        app.applicationScope.launch {
            try {
                app.smsRepository.addIncoming(sms)
                PersistSmsWorker.enqueue(app)
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
