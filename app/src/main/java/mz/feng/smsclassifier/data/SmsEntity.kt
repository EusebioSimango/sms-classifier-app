package mz.feng.smsclassifier.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import mz.feng.smsclassifier.sms.IncomingSms

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
)

fun SmsEntity.toIncomingSms(): IncomingSms =
    IncomingSms(
        sender = sender,
        body = body,
        receivedAtMillis = receivedAtMillis,
    )

fun IncomingSms.toEntity(): SmsEntity =
    SmsEntity(
        sender = sender,
        body = body,
        receivedAtMillis = receivedAtMillis,
    )
