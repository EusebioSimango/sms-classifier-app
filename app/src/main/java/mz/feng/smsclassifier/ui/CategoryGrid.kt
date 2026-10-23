package mz.feng.smsclassifier.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mz.feng.smsclassifier.sms.SenderThread
import mz.feng.smsclassifier.sms.SmsCategory
import mz.feng.smsclassifier.sms.asPreview

@Composable
fun CategoryGrid(
    summaries: List<CategorySummary>,
    onOpenCategory: (SmsCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        summaries.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                row.forEach { summary ->
                    CategoryCard(
                        summary = summary,
                        onClick = { onOpenCategory(summary.category) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size == 1) {
                    androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    summary: CategorySummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = summary.category.icon(),
                contentDescription = summary.category.label,
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(summary.category.label, style = MaterialTheme.typography.titleSmall)
            Text(
                text = if (summary.count == 1) "1 SMS" else "${summary.count} SMS",
                style = MaterialTheme.typography.bodySmall,
            )
            summary.lastAtMillis?.let { millis ->
                Text(formatClock(millis), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun ThreadRow(
    thread: SenderThread,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = thread.sender.ifBlank { "(sem remetente)" },
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = classificationCaption(thread.lastMessage),
                    style = MaterialTheme.typography.labelMedium,
                )
                Text(
                    text = thread.lastMessage.body.asPreview(70),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                )
            }
            Text(
                text = formatClock(thread.lastMessage.receivedAtMillis),
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}
