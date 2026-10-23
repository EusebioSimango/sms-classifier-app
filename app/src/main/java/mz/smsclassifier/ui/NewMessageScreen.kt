package mz.smsclassifier.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import mz.smsclassifier.sms.SavedContact
import mz.smsclassifier.sms.SimLine
import mz.smsclassifier.sms.containsFolded
import mz.smsclassifier.sms.isReplyable

@Composable
fun NewMessageScreen(
    contacts: List<SavedContact>,
    sending: Boolean,
    sendFailed: Boolean,
    onBack: () -> Unit,
    onContactsPermissionGranted: () -> Unit,
    simLines: List<SimLine> = emptyList(),
    selectedSimId: Int? = null,
    onCycleSim: () -> Unit = {},
    onSimsReady: () -> Unit = {},
    onSend: (number: String, body: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var number by rememberSaveable { mutableStateOf("") }
    var draft by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val contactsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) onContactsPermissionGranted()
    }
    val simLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) onSimsReady()
    }
    val sendLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted && isReplyable(number) && draft.isNotBlank()) {
            onSend(number.trim(), draft.trim())
        }
    }
    val contactsGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_CONTACTS,
    ) == PackageManager.PERMISSION_GRANTED
    val needle = number.trim()
    val suggestions = if (needle.length < 2 || !contactsGranted) {
        emptyList()
    } else {
        contacts.filter { contact ->
            contact.name.containsFolded(needle) ||
                contact.number.containsFolded(needle)
        }.distinctBy { it.number }.take(6)
    }
    val canSend = isReplyable(number) && draft.isNotBlank() && !sending

    fun submit() {
        if (!canSend) return
        val smsGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) ==
            PackageManager.PERMISSION_GRANTED
        if (smsGranted) {
            onSend(number.trim(), draft.trim())
        } else {
            sendLauncher.launch(Manifest.permission.SEND_SMS)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TextButton(onClick = onBack) { Text("Voltar") }
        Text("Nova mensagem", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(
            value = number,
            onValueChange = { number = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Para") },
            placeholder = { Text("Número ou nome") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
        )
        if (!isReplyable(number) && number.isNotBlank() && suggestions.isEmpty()) {
            Text(
                text = "Escreve um número de telefone. Códigos como Vodacom não recebem resposta.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (!contactsGranted) {
            TextButton(onClick = { contactsLauncher.launch(Manifest.permission.READ_CONTACTS) }) {
                Text("Usar os nomes e as fotos dos contactos")
            }
        }
        suggestions.forEach { contact ->
            ListItem(
                leadingContent = { ContactAvatar(photoUri = contact.photoUri) },
                headlineContent = { Text(contact.name) },
                supportingContent = { Text(contact.number) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { number = contact.number },
            )
        }
        if (sendFailed) {
            Text(
                text = "Não foi possível enviar. Tenta outra vez.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Mensagem") },
                shape = RoundedCornerShape(24.dp),
                maxLines = 4,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { submit() }),
            )
            SimPicker(
                lines = simLines,
                selectedId = selectedSimId,
                onRequestPermission = { simLauncher.launch(Manifest.permission.READ_PHONE_STATE) },
                onCycle = onCycleSim,
            )
            FilledIconButton(onClick = ::submit, enabled = canSend) {
                if (sending) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar")
                }
            }
        }
        Text(
            text = "A mensagem sai por SMS só para este número.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
