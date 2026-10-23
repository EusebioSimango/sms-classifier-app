package mz.smsclassifier

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import mz.smsclassifier.classifier.ClassifyPendingSms
import mz.smsclassifier.classifier.OnnxSmsClassifier
import mz.smsclassifier.data.AppDatabase
import mz.smsclassifier.data.DatabaseKeyStore
import mz.smsclassifier.data.RoomSmsRepository
import mz.smsclassifier.settings.ThemePreferences
import mz.smsclassifier.sms.InboxSmsReader
import mz.smsclassifier.sms.InboxSync
import mz.smsclassifier.sms.SmsRepository
import mz.smsclassifier.sms.SmsSender

class SmsClassifierApp : Application() {
    val applicationScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    lateinit var smsRepository: SmsRepository
        private set

    lateinit var inboxSync: InboxSync
        private set

    lateinit var classifyPendingSms: ClassifyPendingSms
        private set

    lateinit var smsSender: SmsSender
        private set

    lateinit var themePreferences: ThemePreferences
        private set

    override fun onCreate() {
        super.onCreate()
        themePreferences = ThemePreferences(this)
        val database = AppDatabase.create(this, DatabaseKeyStore(this))
        val repository = RoomSmsRepository(database.smsDao())
        smsRepository = repository
        inboxSync = InboxSync(
            reader = InboxSmsReader(this),
            repository = repository,
        )
        classifyPendingSms = ClassifyPendingSms(
            store = repository,
            classifier = OnnxSmsClassifier(this),
        )
        smsSender = SmsSender(this)
    }
}
