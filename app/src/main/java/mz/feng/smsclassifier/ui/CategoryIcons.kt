package mz.feng.smsclassifier.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.ui.graphics.vector.ImageVector
import mz.feng.smsclassifier.sms.SmsCategory

fun SmsCategory.icon(): ImageVector = when (this) {
    SmsCategory.PERSONAL -> Icons.Outlined.Person
    SmsCategory.MOBILE_WALLET -> Icons.Outlined.AccountBalanceWallet
    SmsCategory.BANK -> Icons.Outlined.AccountBalance
    SmsCategory.OPERATOR -> Icons.Outlined.PhoneAndroid
    SmsCategory.OTHER -> Icons.Outlined.MoreHoriz
    SmsCategory.SUSPICIOUS -> Icons.Outlined.WarningAmber
    SmsCategory.ADS -> Icons.Outlined.Campaign
    SmsCategory.GAMBLING -> Icons.Outlined.SportsEsports
}
