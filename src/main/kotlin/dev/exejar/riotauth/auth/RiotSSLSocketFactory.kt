package dev.exejar.riotauth.auth

import java.net.InetAddress
import java.net.Socket
import java.security.SecureRandom
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

class RiotSSLSocketFactory : SSLSocketFactory() {
    private val sslCtx = SSLContext.getInstance("TLS").also { it.init(null, null, SecureRandom()) }
    private val delegate = sslCtx.socketFactory

    private fun Socket?.insertRiotParams(): Socket? =
        (this as? SSLSocket)?.apply {
            val parameters = this.sslParameters
            parameters.namedGroups = arrayOf(
                "x25519",
                "secp256r1",
                "secp384r1"
            )
            parameters.applicationProtocols = arrayOf("http/1.1")
            parameters.useCipherSuitesOrder = false
            parameters.protocols = arrayOf(
                "TLSv1.3"
            )
            parameters.cipherSuites = arrayOf(
                "TLS_CHACHA20_POLY1305_SHA256",
                "TLS_AES_128_GCM_SHA256",
                "TLS_AES_256_GCM_SHA384",
                "ECDHE-ECDSA-CHACHA20-POLY1305",
                "ECDHE-RSA-CHACHA20-POLY1305",
                "ECDHE-ECDSA-AES128-GCM-SHA256",
                "ECDHE-RSA-AES128-GCM-SHA256",
                "ECDHE-ECDSA-AES256-GCM-SHA384",
                "ECDHE-RSA-AES256-GCM-SHA384",
                "ECDHE-ECDSA-AES128-SHA",
                "ECDHE-RSA-AES128-SHA",
                "ECDHE-ECDSA-AES256-SHA",
                "ECDHE-RSA-AES256-SHA",
                "AES128-GCM-SHA256",
                "AES256-GCM-SHA384",
                "AES128-SHA",
                "AES256-SHA",
                "DES-CBC3-SHA",  // most likely not available
            )
            parameters.signatureSchemes = arrayOf(
                "ecdsa_secp256r1_sha256",
                "rsa_pss_rsae_sha256",
                "rsa_pkcs1_sha256",
                "ecdsa_secp384r1_sha384",
                "rsa_pss_rsae_sha384",
                "rsa_pkcs1_sha384",
                "rsa_pss_rsae_sha512",
                "rsa_pkcs1_sha512",
                "rsa_pkcs1_sha1",  // will get ignored and won't be negotiated
            )
        }

    override fun createSocket(plain: Socket?, host: String?, port: Int, autoClose: Boolean): Socket? =
        delegate.createSocket(plain, host, port, autoClose).insertRiotParams()

    override fun createSocket(host: String?, port: Int): Socket? =
        delegate.createSocket(host, port).insertRiotParams()

    override fun createSocket(host: String?, port: Int, localHost: InetAddress?, localPort: Int): Socket? =
        delegate.createSocket(host, port, localHost, localPort).insertRiotParams()

    override fun createSocket(host: InetAddress?, port: Int): Socket? =
        delegate.createSocket(host, port).insertRiotParams()

    override fun createSocket(address: InetAddress?, port: Int, localAddress: InetAddress?, localPort: Int): Socket? =
        delegate.createSocket(address, port, localAddress, localPort).insertRiotParams()

    override fun getDefaultCipherSuites(): Array<String> = delegate.defaultCipherSuites
    override fun getSupportedCipherSuites(): Array<String> = delegate.supportedCipherSuites
}