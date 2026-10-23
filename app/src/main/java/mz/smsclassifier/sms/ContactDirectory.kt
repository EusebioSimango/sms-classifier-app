package mz.smsclassifier.sms

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract.CommonDataKinds.Phone
import androidx.core.content.ContextCompat

data class SavedContact(val name: String, val number: String, val photoUri: String? = null)

class ContactDirectory(context: Context) {
    private val appContext = context.applicationContext

    fun load(): List<SavedContact> {
        val granted = ContextCompat.checkSelfPermission(appContext, Manifest.permission.READ_CONTACTS) ==
            PackageManager.PERMISSION_GRANTED
        if (!granted) return emptyList()
        val contacts = mutableListOf<SavedContact>()
        val projection = arrayOf(Phone.DISPLAY_NAME, Phone.NUMBER, Phone.PHOTO_THUMBNAIL_URI)
        appContext.contentResolver.query(Phone.CONTENT_URI, projection, null, null, Phone.DISPLAY_NAME)
            ?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(Phone.DISPLAY_NAME)
                val numberIndex = cursor.getColumnIndex(Phone.NUMBER)
                val photoIndex = cursor.getColumnIndex(Phone.PHOTO_THUMBNAIL_URI)
                if (nameIndex < 0 || numberIndex < 0) return emptyList()
                while (cursor.moveToNext()) {
                    val name = cursor.getString(nameIndex)?.trim().orEmpty()
                    val number = cursor.getString(numberIndex)?.trim().orEmpty()
                    val photo = if (photoIndex >= 0) cursor.getString(photoIndex) else null
                    if (name.isNotEmpty() && phoneKey(number).isNotEmpty()) {
                        contacts += SavedContact(name, number, photo)
                    }
                }
            }
        return contacts
    }
}

fun List<SavedContact>.nameFor(sender: String): String? {
    val key = phoneKey(sender)
    if (key.isEmpty()) return null
    return firstOrNull { phoneKey(it.number) == key }?.name
}

fun List<SavedContact>.photoFor(sender: String): String? {
    val key = phoneKey(sender)
    if (key.isEmpty()) return null
    return firstOrNull { phoneKey(it.number) == key && !it.photoUri.isNullOrBlank() }?.photoUri
}