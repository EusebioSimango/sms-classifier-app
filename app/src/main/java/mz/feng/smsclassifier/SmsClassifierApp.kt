package mz.feng.smsclassifier

import android.app.Application
import mz.feng.smsclassifier.sms.InMemorySmsRepository
import mz.feng.smsclassifier.sms.InboxSmsReader
import mz.feng.smsclassifier.sms.SmsRepository

class SmsClassifierApp : Application() {
    val smsRepository: SmsRepository by lazy {
        InMemorySmsRepository(InboxSmsReader(this))
    }
}
