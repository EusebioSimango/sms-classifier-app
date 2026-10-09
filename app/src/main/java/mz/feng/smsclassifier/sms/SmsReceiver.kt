package mz.feng.smsclassifier.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import mz.feng.smsclassifier.BuildConfig
import mz.feng.smsclassifier.SmsClassifierApp

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val sms = SmsParser.fromReceivedIntent(intent) ?: return
        val app = context.applicationContext as SmsClassifierApp
        app.smsRepository.addIncoming(sms)
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "SMS received")
        }
    }

    companion object {
        private const val TAG = "SmsReceiver"
    }
}
