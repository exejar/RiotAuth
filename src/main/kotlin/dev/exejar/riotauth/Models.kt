package dev.exejar.riotauth

import dev.exejar.riotauth.auth.PlayerInfoResponse
import io.ktor.http.*
import kotlinx.serialization.Serializable

object RiotClientVersionResponse {
    @Serializable
    data class Body(
        val status: Int,
        val data: Data
    )
    @Serializable
    data class Data(
        val manifestId: String,
        val branch: String,
        val version: String,
        val buildVersion: String,
        val engineVersion: String,
        val riotClientVersion: String,
        val riotClientBuild: String,
        val buildDate: String
    )
}

open class RiotAccount(
    val accessToken: String,
    val entitlementsToken: String,
    val authCookie: List<Cookie>,
    val playerInfo: PlayerInfoResponse.Body
)