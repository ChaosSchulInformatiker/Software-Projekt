@file:Suppress("HasPlatformType")

package tk.q11mk.utils

import java.util.*
import kotlin.reflect.KProperty

private val publicProperties = run {
    val p = Properties()
    p.load(ClassLoader.getSystemResourceAsStream("public.properties"))
    p
}

private val secretProperties = run {
    val p = Properties()
    p.load(ClassLoader.getSystemResourceAsStream("secret.properties"))
    p
}

/**
 * Get a property from the public.properties file
 */
fun getPublicProperty(key: String) = publicProperties.getProperty(key)

fun setPublicProperty(key: String, value: String) = secretProperties.setProperty(key, value)

fun publicProperty(key: String) = object : kotlin.properties.ReadWriteProperty<Any?, String> {
    override operator fun getValue(thisRef: Any?, property: KProperty<*>) = getPublicProperty(key)

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        setPublicProperty(key, value)
    }
}

/**
 * Get a secret property from the private secret.properties file
 */
fun getSecretProperty(key: String) = secretProperties.getProperty(key)

fun setSecretProperty(key: String, value: String) = secretProperties.setProperty(key, value)

fun secretProperty(key: String) = object : kotlin.properties.ReadWriteProperty<Any?, String> {
    override operator fun getValue(thisRef: Any?, property: KProperty<*>) = getSecretProperty(key)

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        setSecretProperty(key, value)
    }
}