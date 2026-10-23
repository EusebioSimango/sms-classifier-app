package mz.smsclassifier.data

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom

class DatabaseKeyStore(context: Context) {
    private val prefs = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    fun passphrase(): ByteArray {
        val existing = prefs.getString(KEY_PASSPHRASE, null)
        if (existing != null) {
            return Base64.decode(existing, Base64.NO_WRAP)
        }
        val bytes = ByteArray(32).also { SecureRandom().nextBytes(it) }
        prefs.edit()
            .putString(KEY_PASSPHRASE, Base64.encodeToString(bytes, Base64.NO_WRAP))
            .apply()
        return bytes
    }

    companion object {
        private const val PREFS_NAME = "sms_db_key"
        private const val KEY_PASSPHRASE = "passphrase"
    }
}
