package tk.q11mk.accounts

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import tk.q11mk.database.*
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
    val vcValue = /*VCValue("Simon", "Neumann", 12345)*/verificationCache[email]
    var status = /*LoginResponse.Status.SUCCESS*/vcValue?.let {
        if (it.code == code) LoginResponse.Status.SUCCESS else LoginResponse.Status.WRONG_CODE
    } ?: LoginResponse.Status.WRONG_EMAIL
    if (status == LoginResponse.Status.SUCCESS) {
        verificationCache.remove(email)
        println("login $email")
    }

    var id: Long? = null
    fun getId(status: LoginResponse.Status) = id ?: (if (status == LoginResponse.Status.SUCCESS) nextId() else null).also { id = it }

    var clazz: String? = null
    var subjectsCSV: String? = null

    val emailUse = idsTable.getLike<String>("email", email).getOrThrow()
    println(emailUse)
    when (emailUse.size) {
        0 -> idsTable.insertRow(listOf(getId(status), vcValue!!.lastName, vcValue.firstName, email, false)).getOrThrow()
        1 -> {
            id = emailUse[0].first.toLong()
            val account = getAccountFromId(id!!)!!
            if (account.lastName != vcValue!!.lastName || account.firstName != vcValue.firstName) {
                status = LoginResponse.Status.UNEQUAL_DATA
                id = null
            } else {
                val classData = getClassFromId(id!!)
                println(classData)
                clazz = classData.first
                subjectsCSV = classData.second
            }
        }
        else -> status = LoginResponse.Status.UNEXPECTED_ERROR
    }

    return LoginResponse(
        status,
        vcValue?.firstName,
        vcValue?.lastName,
        getId(status),
        clazz,
        subjectsCSV
    )
}

@Serializable
class LoginResponse(
    @Suppress("unused") val status: Status,
    @SerialName("first_name") @Suppress("unused") val firstName: String?,
    @SerialName("last_name") @Suppress("unused") val lastName: String?,
    @Suppress("unused") val id: Long?,
    @SerialName("class") @Suppress("unused") val clazz: String? = null,
    @SerialName("subjects") @Suppress("unused") val subjectsCSV: String? = null
) {
    enum class Status { SUCCESS, WRONG_CODE, WRONG_EMAIL, UNEQUAL_DATA, UNEXPECTED_ERROR }
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

fun changeClassData(id: String, clazz: String, subjectsCSV: String): ChangeClassDataResponse = try {
    if (accountClassesTable.has(id)) {
        accountClassesTable.set("class", id, clazz).getOrThrow()
        accountClassesTable.set("subjects", id, subjectsCSV).getOrThrow()
    } else {
        accountClassesTable.insertRow(listOf(id, clazz, subjectsCSV)).getOrThrow()
    }
    ChangeClassDataResponse(true)
} catch (t: Throwable) { t.printStackTrace(); ChangeClassDataResponse(false) }

@Serializable
data class ChangeClassDataResponse(
    val success: Boolean
)