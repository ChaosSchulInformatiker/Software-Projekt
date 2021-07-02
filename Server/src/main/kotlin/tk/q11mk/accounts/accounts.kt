package tk.q11mk.accounts

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.json.simple.JSONObject
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

suspend fun CoroutineScope.sendCode(firstName: String, lastName: String): RegisterResponse {
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
    return RegisterResponse(email, valid && email != null)
}

@Serializable
class RegisterResponse(
    @SerialName("e_mail") @Suppress("unused") val email: String?,
    @Suppress("unused") val valid: Boolean
)

fun receiveCode(email: String, code: Int): LoginResponse {
    val vcValue = verificationCache[email]
    val status = vcValue?.let {
        if (it.code == code) LoginResponse.Status.SUCCESS else LoginResponse.Status.WRONG_CODE
    } ?: LoginResponse.Status.WRONG_EMAIL
    if (status == LoginResponse.Status.SUCCESS) {
        verificationCache.remove(email)
        println("login $email")
    }
    return LoginResponse(
        status,
        vcValue?.firstName,
        vcValue?.lastName,
        if (status == LoginResponse.Status.SUCCESS) nextId() else null
    )
}

@Serializable
class LoginResponse(
    @Suppress("unused") val status: Status,
    @SerialName("first_name") @Suppress("unused") val firstName: String?,
    @SerialName("last_name") @Suppress("unused") val lastName: String?,
    @Suppress("unused") val id: Long?
) {
    enum class Status { SUCCESS, WRONG_CODE, WRONG_EMAIL }
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