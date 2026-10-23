package mz.smsclassifier.sms

import android.content.Context
import android.provider.Telephony

class InboxSmsReader(context: Context) {
    private val appContext = context.applicationContext

    fun read(limit: Int = DEFAULT_LIMIT): List<IncomingSms> {
        if (!SmsPermissions.areGranted(appContext)) return emptyList()

        return try {
            appContext.contentResolver.query(
                Telephony.Sms.Inbox.CONTENT_URI,
                arrayOf(
                    Telephony.Sms.ADDRESS,
                    Telephony.Sms.BODY,
                    Telephony.Sms.DATE,
                ),
                null,
                null,
                "${Telephony.Sms.DATE} DESC",
            )?.use { cursor ->
                val addressIdx = cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)
                val bodyIdx = cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)
                val dateIdx = cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)
                buildList {
                    var count = 0
                    while (cursor.moveToNext() && count < limit) {
                        add(
                            IncomingSms(
                                sender = cursor.getString(addressIdx).orEmpty(),
                                body = cursor.getString(bodyIdx).orEmpty(),
                                receivedAtMillis = cursor.getLong(dateIdx),
                            ),
                        )
                        count++
                    }
                }
            } ?: emptyList()
        } catch (_: SecurityException) {
            emptyList()
        }
    }

    companion object {
        private const val DEFAULT_LIMIT = 100
    }
}
