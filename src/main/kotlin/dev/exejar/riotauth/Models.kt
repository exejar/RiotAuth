package dev.exejar.riotauth

import kotlinx.serialization.*

@Serializable
data object CookieAuthRequest {
    val acrValues = ""
    val claims = ""
    val clientId = "riot-client"
    val codeChallenge = ""
    val codeChallengeMethod = ""
    val nonce = tokenUrlSafe(16)
    val redirectUri = "http://localhost/redirect"
    val responseType = "token id_token"
    val scope = "openid link ban lol_region account"
}

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
data class AuthResponse(
    val type: String,
    val error: String? = null,
    val response: AuthResponseParameters? = null
)

@Serializable
data class AuthResponseParameters(
    val parameters: Parameters
)

@Serializable
data class Parameters(
    val uri: String
)

data class AccountTokens(
    val accessToken: String,
    val idToken: String
)

@Serializable
data class EntitlementResponse(
    @SerialName("entitlements_token") val entitlementsToken: String
)

@Serializable
data class UserResponse(
    val sub: String
)

data class RiotAccount(
    val accessToken: String,
    val entitlementsToken: String
)
