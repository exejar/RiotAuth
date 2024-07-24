package dev.exejar.riotauth.valorant

import dev.exejar.riotauth.RiotAccount
import dev.exejar.riotauth.auth.PlayerInfoResponse
import io.ktor.http.*

enum class ValorantShard {
    NA, PBE, EU, AP, KR;

    override fun toString() = super.toString().lowercase()
}

class ValorantAccount(
    accessToken: String,
    entitlementsToken: String,
    authCookie: List<Cookie>,
    playerInfo: PlayerInfoResponse.Body,
    val shard: ValorantShard
): RiotAccount(accessToken, entitlementsToken, authCookie, playerInfo)