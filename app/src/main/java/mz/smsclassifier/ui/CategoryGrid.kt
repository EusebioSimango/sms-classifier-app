package mz.smsclassifier.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import mz.smsclassifier.sms.SenderThread
import mz.smsclassifier.sms.SmsCategory
import mz.smsclassifier.sms.asPreview
import mz.smsclassifier.sms.categorize

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
    contactName: String? = null,
    photoUri: String? = null,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        val name = contactName ?: thread.sender.ifBlank { "(sem remetente)" }
        val category = (thread.lastMessage.category ?: categorize(thread.lastMessage)).label
        val unread = thread.unreadCount
        val emphasis = if (unread > 0) FontWeight.Bold else FontWeight.Normal
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box {
                ContactAvatar(photoUri = photoUri, size = 44.dp)
                if (unread > 0) {
                    Badge(
                        modifier = Modifier.align(Alignment.TopStart),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ) {
                        Text(if (unread > 9) "9+" else unread.toString())
                    }
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append(name)
                            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                                append(" · $category")
                            }
                        },
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = emphasis),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = formatListStamp(thread.lastMessage.receivedAtMillis),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp),
                        maxLines = 1,
                    )
                }
                Text(
                    text = thread.lastMessage.body.replace('\n', ' ').asPreview(60),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = emphasis),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
