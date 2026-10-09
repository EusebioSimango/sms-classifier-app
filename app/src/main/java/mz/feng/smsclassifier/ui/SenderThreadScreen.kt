package mz.feng.smsclassifier.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mz.feng.smsclassifier.sms.IncomingSms
import mz.feng.smsclassifier.sms.SenderThread

@Composable
fun SenderThreadScreen(
    thread: SenderThread,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TextButton(onClick = onBack) {
            Text("Voltar")
        }
        Text(
            text = thread.sender.ifBlank { "(sem remetente)" },
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = if (thread.count == 1) "1 SMS" else "${thread.count} SMS",
            style = MaterialTheme.typography.titleMedium,
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                items = thread.messages,
                key = { sms ->
                    "${sms.sender}|${sms.receivedAtMillis}|${sms.body.hashCode()}"
                },
            ) { sms ->
                SmsCard(sms)
            }
        }
    }
}

@Composable
private fun SmsCard(sms: IncomingSms) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(formatTimestamp(sms.receivedAtMillis), style = MaterialTheme.typography.bodySmall)
            Text(sms.body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
