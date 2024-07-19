package dev.exejar.riotauth

import java.security.SecureRandom
import java.util.*

internal fun tokenUrlSafe(nBytes: Int? = null): String {
    val randomBytes = ByteArray(nBytes ?: 16)
    SecureRandom().nextBytes(randomBytes)
    return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes)
}