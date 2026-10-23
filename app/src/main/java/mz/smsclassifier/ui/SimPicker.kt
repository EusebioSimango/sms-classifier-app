package mz.smsclassifier.ui

import android.Manifest
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.content.ContextCompat
import mz.smsclassifier.sms.SimLine

@Composable
fun SimPicker(
    lines: List<SimLine>,
    selectedId: Int?,
    onRequestPermission: () -> Unit,
    onCycle: () -> Unit,
) {
    val context = LocalContext.current
    val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) ==
        PackageManager.PERMISSION_GRANTED
    if (!granted) {
        val telephony = context.getSystemService(TelephonyManager::class.java)
        @Suppress("DEPRECATION")
        val slots = telephony?.phoneCount ?: 1
        if (slots < 2) return
        TextButton(onClick = onRequestPermission) { Text("SIM") }
        return
    }
    if (lines.size < 2) return
    val selected = lines.firstOrNull { it.subscriptionId == selectedId } ?: lines.first()
    TextButton(onClick = onCycle) {
        Text(
            text = selected.label(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}
