package dev.exejar.riotauth

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.api.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import okhttp3.logging.HttpLoggingInterceptor
import java.net.URI
import java.security.KeyStore
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

@OptIn(ExperimentalSerializationApi::class)
object RiotAuthenticator {
    private const val RIOT_AUTH_URL = "https://auth.riotgames.com/api/v1/authorization"

    private val client = HttpClient(OkHttp) {
        engine {
            addInterceptor(HttpLoggingInterceptor().also { it.setLevel(HttpLoggingInterceptor.Level.HEADERS) })
            config {
                sslSocketFactory(RiotSSLSocketFactory(), TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
                    init(null as KeyStore?)
                }.trustManagers.let {
                    require(it.size == 1 && it[0] is X509TrustManager) { "Unexpected default trust managers: ${it.contentToString()}" }
                    it[0] as X509TrustManager
                })
            }
        }
        install(createClientPlugin("headers-fix") {
            on(Send) { req ->
                req.headers.remove("Accept-Charset")
                this.proceed(req)
            }
        })
        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                namingStrategy = JsonNamingStrategy.Builtins.SnakeCase
            })
        }
        install(DefaultRequest) {
            headers {
                append(HttpHeaders.AcceptEncoding, "deflate, gzip, zstd")
                append(HttpHeaders.UserAgent, tokenUrlSafe(111))
                append(HttpHeaders.CacheControl, "no-cache")
                append(HttpHeaders.Accept, "application/json")
                append(HttpHeaders.ContentType, "application/json")
            }
        }
    }

    suspend fun authorize(username: String, password: String) : RiotAccount {
        val cookies = client.post(RIOT_AUTH_URL) {
            setBody(CookieAuthRequest)
        }.setCookie().joinToString("; ") { "${it.name}=${it.value}" }

        val authResponse = client.put(RIOT_AUTH_URL) {
            header(HttpHeaders.Cookie, cookies)
            setBody(AuthRequest(username = username, password = password))
        }.body() as AuthResponse

        when (authResponse.type) {
            "auth_failure" -> throw Exception("Auth Failure: username or password is incorrect")
            "error" -> throw Error(authResponse.error)
        }

        val tokens = parseTokensFromUrl(authResponse.response!!.parameters.uri)

        val entitlementsToken = client.post("https://entitlements.auth.riotgames.com/api/token/v1") {
            header(HttpHeaders.Authorization, "Bearer ${tokens.accessToken}")
        }.bodyAsText()

        return RiotAccount(tokens.accessToken, entitlementsToken)
    }

    private fun parseTokensFromUrl(uri: String): AccountTokens {
        val params = URI(uri).toURL().query.split("&").associate {
            val (key, value) = it.split("=")
            key to value
        }
        return AccountTokens(
            accessToken = params["access_token"] ?: "",
            idToken = params["id_token"] ?: ""
        )
    }
}