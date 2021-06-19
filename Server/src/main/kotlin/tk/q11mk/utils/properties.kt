package tk.q11mk.utils

import java.util.*

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

/**
 * Get a secret property from the private secret.properties file
 */
fun getSecretProperty(key: String) = secretProperties.getProperty(key)