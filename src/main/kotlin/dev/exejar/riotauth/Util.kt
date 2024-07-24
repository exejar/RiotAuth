package dev.exejar.riotauth

import dev.exejar.riotauth.valorant.ValorantAccount
import dev.exejar.riotauth.valorant.ValorantShard
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import java.security.SecureRandom
import java.util.*

val defaultClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}
const val RIOT_CLIENT_PLATFORM = "ew0KCSJwbGF0Zm9ybVR5cGUiOiAiUEMiLA0KCSJwbGF0Zm9ybU9TIjogIldpbmRvd3MiLA0KCSJwbGF0Zm9ybU9TVmVyc2lvbiI6ICIxMC4wLjE5MDQyLjEuMjU2LjY0Yml0IiwNCgkicGxhdGZvcm1DaGlwc2V0IjogIlVua25vd24iDQp9"

internal fun tokenUrlSafe(nBytes: Int? = null): String {
    val randomBytes = ByteArray(nBytes ?: 16)
    SecureRandom().nextBytes(randomBytes)
    return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes)
}

@ExperimentalSerializationApi
object NamingStrategy {
    val pascalCase: JsonNamingStrategy = object : JsonNamingStrategy {
        override fun serialNameForJson(descriptor: SerialDescriptor, elementIndex: Int, serialName: String): String =
            buildString(serialName.length) {
                var capitalizeNext = true

                serialName.forEachIndexed { index, c ->
                    when {
                        c == '_' -> capitalizeNext = true
                        c.isUpperCase() && index > 0 && serialName[index - 1].isLowerCase() -> {
                            append(c)
                            capitalizeNext = false
                        }
                        capitalizeNext -> {
                            append(c.uppercaseChar())
                            capitalizeNext = false
                        }
                        else -> append(c.lowercaseChar())
                    }
                }
            }

        override fun toString(): String = "dev.exejar.riotauth.NamingStrategy.PascalCase"
    }
}

fun RiotAccount.getValorant(shard: ValorantShard) =
    ValorantAccount(this.accessToken, this.entitlementsToken, this.authCookie, this.playerInfo, shard)

suspend fun getRiotClientVersion() =
    defaultClient.get("https://valorant-api.com/v1/version").body<RiotClientVersionResponse.Body>()