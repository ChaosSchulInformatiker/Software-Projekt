package tk.q11mk.accounts

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.simple.JSONObject
import tk.q11mk.JSONSerializable
import tk.q11mk.mailbot.Mail
import tk.q11mk.nextId
import tk.q11mk.utils.getSecretProperty
import java.util.*
import javax.mail.MessagingException

private val verificationCache = hashMapOf<String, VCValue>()

private class VCValue(
    val firstName: String,
    val lastName: String,
    val code: Int
)

suspend fun CoroutineScope.sendCode(firstName: String, lastName: String): JSONSerializable {
    var email: String? = null
    val code = generateCode()
    val valid = try {
        email = getEmailAccount(firstName, lastName)
        Mail.sendEmail(senderEmail, email, senderPassword, firstName, code)
        true
    } catch (e: EmailTransformException) {
        false
    } catch (e: MessagingException) {
        false
    }
    if (valid) {
        verificationCache[email!!] = VCValue(firstName, lastName, code)
        removeFromVCC(this, email)
    }
    return object : JSONSerializable {
        override fun serialize(): JSONObject {
            val root = JSONObject()
            root["e_mail"] = email
            root["valid"] = valid && email != null
            return root
        }
    }
}

fun receiveCode(email: String, code: Int): JSONSerializable {
    val vcValue = verificationCache[email]
    val status = vcValue?.let {
        if (it.code == code) "SUCCESS" else "WRONG_CODE"
    } ?: "WRONG_EMAIL"
    if (status == "SUCCESS") {
        verificationCache.remove(email)
        println("login $email")
    }
    return object : JSONSerializable {
        override fun serialize() = JSONObject().apply {
            this["status"] = status
            this["first_name"] = vcValue?.firstName
            this["last_name"] = vcValue?.lastName
            this["id"] = if (status == "SUCCESS") nextId() else null
        }
    }
}

private val random = Random()
private fun generateCode(): Int {
    var c = random.nextInt(90_000) + 10_000
    if (c % 10 == 0) c += random.nextInt(10)
    if (c % 100 < 10) c += random.nextInt(10) * 10
    if (c % 1000 < 100) c += random.nextInt(10) * 100
    return c
}

private suspend fun removeFromVCC(c: CoroutineScope, key: String) {
    c.launch {
        delay(1000L * 60 * 15)
        if (verificationCache.remove(key) != null)
            println("timeout $key")
    }
}

private val senderEmail = getSecretProperty("email_address")
private val senderPassword = getSecretProperty("email_password")

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
        else -> throw EmailTransformException
    }
}

private object EmailTransformException : Throwable()

private val capitalLetters = 'A'..'Z'
private val lowercaseLetters = 'a'..'z'