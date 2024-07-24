package dev.exejar.riotauth.auth

import dev.exejar.riotauth.tokenUrlSafe
import kotlinx.serialization.*

@Serializable
data class AuthCookiesPost(
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
data class AuthRequestPut(
    val language: String = "en_US",
    val password: String,
    val region: String? = null,
    val remember: Boolean = false,
    val type: String = "auth",
    val username: String
)

@Serializable
data class EntitlementsTokenResponse(
    val entitlementsToken: String
)
object AuthRequestResponse {
    @Serializable
    data class Body(
        val type: String,
        val error: String? = null,
        val response: Response? = null
    )
    @Serializable
    data class Response(
        val parameters: Parameters
    )
    @Serializable
    data class Parameters(
        val uri: String
    )
}

object PlayerInfoResponse {
    @Serializable
    data class Body(
        val country: String,
        val sub: String,
        val emailVerified: Boolean,
        val countryAt: Long?,
        val pw: Password,
        val phoneNumberVerified: Boolean,
        val accountVerified: Boolean? = null,
        val federatedIdentityProviders: List<String> = listOf(),
        val playerLocale: String?,
        val acct: Account,
        val age: Int? = null,
        val jti: String,
        val affinity: Map<String, String> = mapOf()
    )
    @Serializable
    data class Password(
        val cngAt: Long,
        val reset: Boolean,
        val mustReset: Boolean
    )
    @Serializable
    data class Account(
        val type: Int,
        val state: String,
        val adm: Boolean,
        val gameName: String,
        val tagLine: String,
        val createdAt: Long
    )
}

@Serializable
data class ValorantGeoPut(
    val idToken: String
)
object ValorantGeoResponse {
    @Serializable
    data class Body(
        val token: String,
        val affinities: Affinities
    )
    @Serializable
    data class Affinities(
        val pbe: String,
        val live: String
    )
}