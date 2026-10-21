package mz.feng.smsclassifier

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import mz.feng.smsclassifier.data.AppDatabase
import mz.feng.smsclassifier.data.DatabaseKeyStore
import mz.feng.smsclassifier.data.RoomSmsRepository
import mz.feng.smsclassifier.settings.ThemePreferences
import mz.feng.smsclassifier.sms.InboxSmsReader
import mz.feng.smsclassifier.sms.InboxSync
import mz.feng.smsclassifier.sms.SmsRepository

class SmsClassifierApp : Application() {
    val applicationScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    lateinit var smsRepository: SmsRepository
        private set

    lateinit var inboxSync: InboxSync
        private set

    lateinit var themePreferences: ThemePreferences
        private set

    override fun onCreate() {
        super.onCreate()
        themePreferences = ThemePreferences(this)
        val database = AppDatabase.create(this, DatabaseKeyStore(this))
        smsRepository = RoomSmsRepository(database.smsDao())
        inboxSync = InboxSync(
            reader = InboxSmsReader(this),
            repository = smsRepository,
        )
    }
}
