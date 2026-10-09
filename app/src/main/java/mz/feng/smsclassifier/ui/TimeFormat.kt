package mz.feng.smsclassifier.ui

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal fun formatTimestamp(millis: Long): String =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.systemDefault())
        .format(Instant.ofEpochMilli(millis))
