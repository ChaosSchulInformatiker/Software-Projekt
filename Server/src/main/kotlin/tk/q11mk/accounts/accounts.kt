package tk.q11mk.accounts

import org.json.simple.JSONObject
import tk.q11mk.JSONSerializable
import tk.q11mk.mailbot.Mail
import tk.q11mk.utils.getSecretProperty
import javax.mail.MessagingException

fun sendCode(firstName: String, lastName: String): JSONSerializable {
    val email = getEmailAccount(firstName, lastName)
    val valid = try {
        Mail.sendEmail(senderEmail, email, senderPassword, firstName, 12345)
        true
    } catch (e: MessagingException) {
        false
    }
    return object : JSONSerializable {
        override fun serialize(): JSONObject {
            val root = JSONObject()
            root["first_name"] = firstName
            root["last_name"] = lastName
            root["e_mail"] = email
            root["valid"] = valid && email != null
            return root
        }
    }
}

private val senderEmail = getSecretProperty("email")
private val senderPassword = getSecretProperty("password")

fun getEmailAccount(firstName: String, lastName: String) = try {
    buildString {
        for (c in firstName)
            appendTransformedChar(c)
        append('.')
        for (c in lastName)
            appendTransformedChar(c)
        append("@maristenkolleg.de")
    }
} catch (e: EmailTransformException) {
    null
}

private fun StringBuilder.appendTransformedChar(c: Char) {
    when(c) {
        in capitalLetters -> append(c + 0x20)
        in lowercaseLetters -> append(c)
        'ä', 'Ä' -> append("ae")
        'ö', 'Ö' -> append("oe")
        'ü', 'Ü' -> append("ue")
        'ß' -> append("ss")
        else -> throw EmailTransformException
    }
}

private object EmailTransformException : Throwable()

private val capitalLetters = 'A'..'Z'
private val lowercaseLetters = 'a'..'z'