package dev.exejar.riotauth

suspend fun main() {
    RiotAuthenticator.authorize("username", "password")
}