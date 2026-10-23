package mz.smsclassifier.sms

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.telephony.SmsManager
import android.telephony.SubscriptionManager
import java.util.UUID
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

class SmsSender(context: Context) {
    private val appContext = context.applicationContext

    suspend fun send(destination: String, body: String, subscriptionId: Int? = null): Boolean {
        if (!isReplyable(destination) || body.isBlank()) return false
        val address = replyAddress(destination)
        val manager = smsManager(subscriptionId)
        val parts = manager.divideMessage(body)
        if (parts.isEmpty()) return false
        return suspendCancellableCoroutine { cont ->
            val token = UUID.randomUUID().toString()
            val action = "$ACTION_SENT.$token"
            var remaining = parts.size
            var failed = false
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (resultCode != Activity.RESULT_OK) failed = true
                    remaining -= 1
                    if (remaining > 0) return
                    unregister()
                    if (cont.isActive) cont.resume(!failed)
                }

                fun unregister() {
                    runCatching { appContext.unregisterReceiver(this) }
                }
            }
            val filter = IntentFilter(action)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                appContext.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                appContext.registerReceiver(receiver, filter)
            }
            cont.invokeOnCancellation { receiver.unregister() }
            val intents = ArrayList<PendingIntent>(parts.size)
            val mutable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0
            parts.indices.forEach { index ->
                intents += PendingIntent.getBroadcast(
                    appContext,
                    token.hashCode() + index,
                    Intent(action).setPackage(appContext.packageName),
                    mutable,
                )
            }
            try {
                if (parts.size == 1) {
                    manager.sendTextMessage(address, null, parts.first(), intents.first(), null)
                } else {
                    manager.sendMultipartTextMessage(address, null, parts, intents, null)
                }
            } catch (_: Exception) {
                receiver.unregister()
                if (cont.isActive) cont.resume(false)
            }
        }
    }

    private fun smsManager(subscriptionId: Int?): SmsManager {
        val base = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            appContext.getSystemService(SmsManager::class.java)
        } else {
            @Suppress("DEPRECATION")
            SmsManager.getDefault()
        }
        if (subscriptionId == null || subscriptionId == SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
            return base
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            base.createForSubscriptionId(subscriptionId)
        } else {
            @Suppress("DEPRECATION")
            SmsManager.getSmsManagerForSubscriptionId(subscriptionId)
        }
    }

    private companion object {
        const val ACTION_SENT = "mz.smsclassifier.SMS_SENT"
    }
}
