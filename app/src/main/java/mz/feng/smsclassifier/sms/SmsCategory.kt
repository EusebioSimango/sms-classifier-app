package mz.feng.smsclassifier.sms

enum class SmsCategory(val label: String) {
    PERSONAL("Pessoal"),
    MOBILE_WALLET("Carteira móvel"),
    BANK("Bancário"),
    OPERATOR("Operadora"),
    OTHER("Outros"),
    SUSPICIOUS("Suspeitas"),
    ADS("Publicidade"),
    GAMBLING("Jogos e apostas"),
}

fun categorize(sms: IncomingSms): SmsCategory {
    val sender = sms.sender.lowercase()
    val body = sms.body.lowercase()
    val hay = "$sender $body"

    if (containsAny(hay, "mpesa", "m-pesa", "emola", "e-mola", "mkesh", "m-kesh")) {
        return SmsCategory.MOBILE_WALLET
    }
    if (containsAny(hay, "millennium", "bimbim", "bci", "standard bank", "moza banco") ||
        sender.contains("bim")
    ) {
        return SmsCategory.BANK
    }
    if (sender == "vm" ||
        containsAny(sender, "vodacom", "tmcel", "movitel") ||
        containsAny(hay, "vodacom", "tmcel", "movitel")
    ) {
        return SmsCategory.OPERATOR
    }
    if (containsAny(hay, "aposta", "apostas", " diversao", " diversão")) {
        return SmsCategory.GAMBLING
    }
    if (containsAny(hay, "ganhe agora", "clique aqui", "urgente transfira", "conta bloqueada")) {
        return SmsCategory.SUSPICIOUS
    }
    if (containsAny(hay, "promo", "promocao", "desconto", "oferta", "ganhe")) {
        return SmsCategory.ADS
    }
    if (sender.count { it.isDigit() } >= 8) {
        return SmsCategory.PERSONAL
    }
    return SmsCategory.OTHER
}

fun categorizeThread(thread: SenderThread): SmsCategory {
    val votes = thread.messages.groupingBy { categorize(it) }.eachCount()
    return votes.maxByOrNull { it.value }?.key ?: SmsCategory.OTHER
}

private fun containsAny(hay: String, vararg needles: String): Boolean =
    needles.any { hay.contains(it) }
