package dev.exejar.riotauth

import dev.exejar.riotauth.auth.AuthRequestResponse
import dev.exejar.riotauth.auth.RiotAuthenticator
import dev.exejar.riotauth.valorant.ValorantShard
import dev.exejar.riotauth.valorant.getSkinLevel
import dev.exejar.riotauth.valorant.getStorefront
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

suspend fun main() {
    val account = RiotAuthenticator.authorize("username", "password")

    println("Access Token: ${account.accessToken}")
    println("Entitlements Token: ${account.entitlementsToken}")
    println("PUUID: ${account.playerInfo.sub}")

    val valAccount = account.getValorant(ValorantShard.NA)

    val storefront = valAccount.getStorefront()

    storefront.skinsPanelLayout.singleItemOffers.forEach {
        val skinLevel = getSkinLevel(it)
        if (skinLevel.status != 200)
            error("Failed to retrieve skin level information")

        println("Skin ${skinLevel.data.displayName} available for Purchase")
    }
}