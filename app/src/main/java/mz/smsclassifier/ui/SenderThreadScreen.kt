package mz.smsclassifier.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import mz.smsclassifier.sms.IncomingSms
import mz.smsclassifier.sms.SenderThread
import mz.smsclassifier.sms.isReplyable

@Composable
fun SenderThreadScreen(
    thread: SenderThread,
    title: String = thread.sender,
    photoUri: String? = null,
    sending: Boolean,
    sendFailed: Boolean,
    onBack: () -> Unit,
    onSend: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val replyable = isReplyable(thread.sender)
    var draft by rememberSaveable(thread.sender) { mutableStateOf("") }
    var wasSending by rememberSaveable(thread.sender) { mutableStateOf(false) }
    LaunchedEffect(sending, sendFailed) {
        if (wasSending && !sending && !sendFailed) draft = ""
        wasSending = sending
    }
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted && draft.isNotBlank()) onSend(draft)
    }
    fun submit() {
        val text = draft.trim()
        if (text.isEmpty() || sending) return
        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) ==
            PackageManager.PERMISSION_GRANTED
        if (granted) {
            onSend(text)
        } else {
            permissionLauncher.launch(Manifest.permission.SEND_SMS)
        }
    }

    Column(modifier = modifier.fillMaxSize().imePadding()) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBackIosNew, contentDescription = "Voltar")
                }
                ContactAvatar(photoUri = photoUri, size = 48.dp)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = title.ifBlank { "(sem remetente)" },
                        style = MaterialTheme.typography.titleLarge,
                    )
                    if (title != thread.sender && thread.sender.isNotBlank()) {
                        Text(
                            text = thread.sender,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            reverseLayout = true,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items(
                items = chatRows(thread.messages),
                key = { row ->
                    when (row) {
                        is ChatRow.Day -> "day-${row.label}"
                        is ChatRow.Bubble ->
                            "${row.sms.outgoing}|${row.sms.receivedAtMillis}|${row.sms.body.hashCode()}"
                    }
                },
            ) { row ->
                when (row) {
                    is ChatRow.Day -> DaySeparator(row.label)
                    is ChatRow.Bubble -> MessageBubble(row.sms)
                }
            }
        }
        if (replyable) {
            ReplyComposer(
                draft = draft,
                sending = sending,
                sendFailed = sendFailed,
                onDraftChange = { draft = it },
                onSend = ::submit,
            )
        }
    }
}

@Composable
private fun DaySeparator(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 2.dp),
    )
}

@Composable
private fun MessageBubble(sms: IncomingSms) {
    val outgoing = sms.outgoing
    val shape = if (outgoing) {
        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomEnd = 4.dp, bottomStart = 18.dp)
    } else {
        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomEnd = 18.dp, bottomStart = 4.dp)
    }
    val container = if (outgoing) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }
    val content = if (outgoing) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .align(if (outgoing) Alignment.CenterEnd else Alignment.CenterStart)
                .widthIn(max = 300.dp),
            horizontalAlignment = if (outgoing) Alignment.End else Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = formatClock(sms.receivedAtMillis),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Surface(shape = shape, color = container) {
                Text(
                    text = sms.body,
                    style = MaterialTheme.typography.bodyLarge,
                    color = content,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                )
            }
            if (!outgoing) {
                Text(
                    text = classificationCaption(sms),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ReplyComposer(
    draft: String,
    sending: Boolean,
    sendFailed: Boolean,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    Surface(color = MaterialTheme.colorScheme.surface) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (sendFailed) {
                Text(
                    text = "Não foi possível enviar. Tenta outra vez.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = draft,
                    onValueChange = onDraftChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Mensagem") },
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onSend() }),
                )
                FilledIconButton(
                    onClick = onSend,
                    enabled = draft.isNotBlank() && !sending,
                ) {
                    if (sending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar")
                    }
                }
            }
            Text(
                text = "A resposta sai por SMS só para este número.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
