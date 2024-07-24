package dev.exejar.riotauth.auth

import dev.exejar.riotauth.RiotAccount
import dev.exejar.riotauth.tokenUrlSafe
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import okhttp3.logging.HttpLoggingInterceptor
import java.security.KeyStore
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

@OptIn(ExperimentalSerializationApi::class)
object RiotAuthenticator {
    private const val RIOT_AUTH_URL = "https://auth.riotgames.com/api/v1/authorization"

    private val authClient = HttpClient(OkHttp) {
        engine {
            addInterceptor { chain ->
                val req = chain.request().newBuilder()
                    .removeHeader("Accept-Charset")
                    .build()
                chain.proceed(req)
            }
            addInterceptor(HttpLoggingInterceptor().also { it.setLevel(HttpLoggingInterceptor.Level.BASIC) })
            config {
                sslSocketFactory(RiotSSLSocketFactory(), TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
                    init(null as KeyStore?)
                }.trustManagers.let {
                    require(it.size == 1 && it[0] is X509TrustManager) { "Unexpected default trust managers: ${it.contentToString()}" }
                    it[0] as X509TrustManager
                })
            }
        }
        install(HttpCookies) {
            storage = object : CookiesStorage {
                private val storage = mutableMapOf<String, Cookie>()
                private val mutex = Mutex()

                override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
                    mutex.withLock {
                        if (cookie.name == "asid") {
                            storage[cookie.name] = cookie
                        }
                    }
                }
                override suspend fun get(requestUrl: Url): List<Cookie> {
                    return mutex.withLock {
                        storage.values.toList()
                    }
                }
                override fun close() = Unit
            }
        }
        install(ContentNegotiation) {
            json(Json {
                encodeDefaults = true
                ignoreUnknownKeys = true
                namingStrategy = JsonNamingStrategy.Builtins.SnakeCase
            })
        }
        install(ContentEncoding) {
            gzip()
            deflate()
        }
        install(DefaultRequest) {
            headers {
                append(HttpHeaders.UserAgent, tokenUrlSafe(111))
                append(HttpHeaders.CacheControl, "no-cache")
                append(HttpHeaders.ContentType, "application/json")
            }
        }
    }

    suspend fun authorize(username: String, password: String) : RiotAccount {
        authClient.post(RIOT_AUTH_URL) {
            setBody(AuthCookiesPost())
        }

        val authResponse = authClient.put(RIOT_AUTH_URL) {
            setBody(AuthRequestPut(username = username, password = password))
        }.body<AuthRequestResponse.Body>()

        when (authResponse.type) {
            "auth_failure" -> error("Auth Failure: username or password is incorrect")
            "error" -> error(authResponse.error ?: "Unknown Auth Error")
        }

        val tokens = parseTokensFromUrl(authResponse.response!!.parameters.uri)

        val accessToken = tokens["access_token"] ?: error("access_token not found when parsing URI")
        val entitlementsToken = authClient.post("https://entitlements.auth.riotgames.com/api/token/v1") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
        }.body<EntitlementsTokenResponse>().entitlementsToken

        val playerInfo = authClient.get("https://auth.riotgames.com/userinfo") {
            bearerAuth(accessToken)
        }.body<PlayerInfoResponse.Body>()

        return RiotAccount(accessToken, entitlementsToken, authClient.cookies(RIOT_AUTH_URL), playerInfo)
    }

    private fun parseTokensFromUrl(uri: String): Map<String, String> {
        val fragment = uri.substringAfter("#")
        return fragment.split("&").associate {
            val (key, value) = it.split("=")
            key to value
        }
    }
}

