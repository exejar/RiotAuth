package dev.exejar.riotauth

import kotlinx.serialization.*

@Serializable
data class CookieAuthRequest(
    val acrValues: String = "",
    val claims: String = "",
    val clientId: String = "riot-client",
    val codeChallenge: String = "",
    val codeChallengeMethod: String = "",
    val nonce: String = tokenUrlSafe(16),
    val redirectUri: String = "http://localhost/redirect",
    val responseType: String = "token id_token",
    val scope: String = "openid link ban lol_region account"
)

@Serializable
data class AuthRequest(
    val language: String = "en_US",
    val password: String,
    val region: String? = null,
    val remember: Boolean = false,
    val type: String = "auth",
    val username: String
)

@Serializable
data class AuthResponseBody(
    val type: String,
    val error: String? = null,
    val response: Response? = null
)

@Serializable
data class Response(
    val parameters: Parameters,
)

@Serializable
data class Parameters(
    val uri: String
)

data class RiotAccount(
    val accessToken: String,
    val entitlementsToken: String
)
