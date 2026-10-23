package mz.smsclassifier.sms

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import mz.smsclassifier.MainActivity
import mz.smsclassifier.R

const val OPEN_SENDER_EXTRA = "mz.smsclassifier.OPEN_SENDER"

private const val CHANNEL_ID = "incoming_sms"
private const val EXTRA_SENDER = "mz.smsclassifier.NOTIFY_SENDER"
private const val EXTRA_AT = "mz.smsclassifier.NOTIFY_AT"
private const val ASKED_NOTIFICATIONS = "asked_post_notifications"

object VisibleConversation {
    @Volatile
    var sender: String? = null

    @Volatile
    var resumed: Boolean = false

    fun isOpen(incoming: String): Boolean = resumed && sameParty(sender, incoming)
}

fun sameParty(open: String?, incoming: String): Boolean {
    if (open == null) return false
    if (open == incoming) return true
    val left = phoneKey(open)
    val right = phoneKey(incoming)
    return left.isNotEmpty() && left == right
}

fun notificationId(sender: String, receivedAtMillis: Long): Int {
    val hash = 31 * sender.hashCode() + receivedAtMillis.hashCode()
    return hash and Int.MAX_VALUE
}

object IncomingSmsNotifier {
    fun shouldAskPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < 33) return false
        if (isGranted(context)) return false
        return !context.getSharedPreferences("ui", Context.MODE_PRIVATE)
            .getBoolean(ASKED_NOTIFICATIONS, false)
    }

    fun markPermissionAsked(context: Context) {
        context.getSharedPreferences("ui", Context.MODE_PRIVATE)
            .edit()
            .putBoolean(ASKED_NOTIFICATIONS, true)
            .apply()
    }

    fun show(context: Context, sms: IncomingSms) {
        if (sms.outgoing || VisibleConversation.isOpen(sms.sender)) return
        if (!canPost(context)) return
        ensureChannel(context)
        val manager = manager(context) ?: return
        manager.notify(notificationId(sms.sender, sms.receivedAtMillis), build(context, sms, sms.category))
    }

    fun refreshCategories(context: Context, messages: List<IncomingSms>) {
        val manager = manager(context) ?: return
        manager.activeNotifications.forEach { status ->
            if (status.notification.channelId != CHANNEL_ID) return@forEach
            val sender = status.notification.extras.getString(EXTRA_SENDER) ?: return@forEach
            val at = status.notification.extras.getLong(EXTRA_AT, Long.MIN_VALUE)
            val sms = messages.firstOrNull {
                !it.outgoing && it.sender == sender && it.receivedAtMillis == at && it.category != null
            } ?: return@forEach
            manager.notify(status.id, build(context, sms, sms.category))
        }
    }

    fun cancel(context: Context, sender: String) {
        val manager = manager(context) ?: return
        manager.activeNotifications.forEach { status ->
            val stored = status.notification.extras.getString(EXTRA_SENDER) ?: return@forEach
            if (sameParty(stored, sender)) manager.cancel(status.id)
        }
    }

    private fun build(context: Context, sms: IncomingSms, category: SmsCategory?): Notification {
        val title = ContactDirectory(context).load().nameFor(sms.sender)
            ?: sms.sender.ifBlank { "Mensagem" }
        val open = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(OPEN_SENDER_EXTRA, sms.sender)
        }
        val content = PendingIntent.getActivity(
            context,
            notificationId(sms.sender, sms.receivedAtMillis),
            open,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val locked = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_sms)
            .setContentTitle("Nova mensagem")
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_sms)
            .setContentTitle(title)
            .setContentText(category?.label ?: "Nova mensagem")
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setPublicVersion(locked)
            .setContentIntent(content)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addExtras(Bundle().apply {
                putString(EXTRA_SENDER, sms.sender)
                putLong(EXTRA_AT, sms.receivedAtMillis)
            })
            .build()
    }

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < 26) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Mensagens novas",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "Aviso quando chega um SMS. O texto da mensagem não é mostrado."
            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }
        manager(context)?.createNotificationChannel(channel)
    }

    private fun canPost(context: Context): Boolean {
        if (!isGranted(context)) return false
        return manager(context)?.areNotificationsEnabled() == true
    }

    private fun isGranted(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < 33) return true
        return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
    }

    private fun manager(context: Context): NotificationManager? =
        context.getSystemService(NotificationManager::class.java)
}
