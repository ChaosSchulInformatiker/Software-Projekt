package tk.q11mk

import tk.q11mk.utils.getSecretProperty
import tk.q11mk.utils.secretProperty
import java.util.*

private val random = Random()

fun nextId(): Long {
    var currentId by secretProperty("current_id")
    var new = currentId.toLongOrNull() ?: random.nextInt(1_000_000_000) + 1_000_000_000L
    new = convolute(new)
    currentId = new.toString()
    return new
}

private val modulo = getSecretProperty("id_modulo").toLong()

private fun convolute(l: Long): Long {
    var l = l + random.nextInt(1_000_000)
    l += modulo - l % modulo
    return  l
}