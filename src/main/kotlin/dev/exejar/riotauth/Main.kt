package dev.exejar.riotauth

suspend fun main() {
    val account = RiotAuthenticator.authorize("username", "password")

    println("Access Token: ${account.accessToken}")
    println("Entitlements Token: ${account.entitlementsToken}")
}