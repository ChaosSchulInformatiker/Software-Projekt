package tk.q11mk.accounts

import tk.q11mk.mailbot.Mail
import tk.q11mk.utils.getSecretProperty

fun sendCode(firstName: String, email: String) {
    Mail.sendEmail(senderEmail, email, senderPassword, firstName, 12345)
}

private val senderEmail = getSecretProperty("email")
private val senderPassword = getSecretProperty("password")

fun getEmailAccount(firstName: String, lastName: String) = buildString {
    for (c in firstName)
        appendTransformedChar(c)
    append('.')
    for (c in lastName)
        appendTransformedChar(c)
    append("@maristenkolleg.de")
}

private fun StringBuilder.appendTransformedChar(c: Char) {
    when(c) {
        in capitalLetters -> append(c + 0x20)
        in lowercaseLetters -> append(c)
        'ä', 'Ä' -> append("ae")
        'ö', 'Ö' -> append("oe")
        'ü', 'Ü' -> append("ue")
        'ß' -> append("ss")
    }
}

private val capitalLetters = 'A'..'Z'
private val lowercaseLetters = 'a'..'z'