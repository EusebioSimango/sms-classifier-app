package mz.smsclassifier.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import mz.smsclassifier.sms.IncomingSms
import mz.smsclassifier.sms.SmsCategory

@Entity(
    tableName = "sms",
    indices = [
        Index(value = ["sender", "body", "receivedAtMillis"], unique = true),
    ],
)
data class SmsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String,
    val body: String,
    val receivedAtMillis: Long,
    val category: String? = null,
    val confidence: Float? = null,
    val modelVersion: String? = null,
    val outgoing: Boolean = false,
    val unread: Boolean = false,
)

fun SmsEntity.toIncomingSms(): IncomingSms =
    IncomingSms(
        sender = sender,
        body = body,
        receivedAtMillis = receivedAtMillis,
        category = SmsCategory.fromStorage(category),
        confidence = confidence,
        outgoing = outgoing,
        unread = unread,
    )

fun IncomingSms.toEntity(): SmsEntity =
    SmsEntity(
        sender = sender,
        body = body,
        receivedAtMillis = receivedAtMillis,
        outgoing = outgoing,
        unread = unread,
    )
