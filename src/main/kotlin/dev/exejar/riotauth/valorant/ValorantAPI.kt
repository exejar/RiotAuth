package dev.exejar.riotauth.valorant

import dev.exejar.riotauth.*
import dev.exejar.riotauth.valorant.ValorantAPI.valClient
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.logging.HttpLoggingInterceptor

@OptIn(ExperimentalSerializationApi::class)
object ValorantAPI {
    val valClient = HttpClient(OkHttp) {
        engine {
            addInterceptor { chain ->
                val req = chain.request().newBuilder()
                    .removeHeader("Accept-Charset")
                    .build()
                chain.proceed(req)
            }
            addInterceptor(HttpLoggingInterceptor().also { it.setLevel(HttpLoggingInterceptor.Level.BASIC) })
        }
        install(ContentNegotiation) {
            json(Json {
                encodeDefaults = true
                ignoreUnknownKeys = true
                namingStrategy = NamingStrategy.pascalCase
            })
        }
        install(DefaultRequest) {
            headers {
                append(HttpHeaders.CacheControl, "no-cache")
                append(HttpHeaders.ContentType, "application/json")
            }
        }
    }
}

suspend fun ValorantAccount.getStorefront(): StorefrontResponse.Body {
    val versionResponse = getRiotClientVersion()

    if (versionResponse.status != 200)
        error("Failed to retrieve Riot Client Version")

    return valClient.get("https://pd.${shard}.a.pvp.net/store/v2/storefront/${playerInfo.sub}") {
        headers {
            append("X-Riot-ClientPlatform", RIOT_CLIENT_PLATFORM)
            append("X-Riot-ClientVersion", versionResponse.data.riotClientVersion)
            append("X-Riot-Entitlements-JWT", entitlementsToken)
            append("Authorization", "Bearer $accessToken")
        }
    }.body<StorefrontResponse.Body>()
}

suspend fun getSkinLevel(uuid: String): WeaponSkinLevelResponse.Body =
    defaultClient.get("https://valorant-api.com/v1/weapons/skinlevels/$uuid").body<WeaponSkinLevelResponse.Body>()
