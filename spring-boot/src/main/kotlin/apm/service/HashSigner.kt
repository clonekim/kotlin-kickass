package apm.service

import apm.config.CipherProperties
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import java.io.IOException
import java.io.InputStream
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.annotation.PostConstruct
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager
import javax.xml.stream.XMLEventReader
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamException


@Component
@EnableConfigurationProperties(CipherProperties::class)
class HashSigner {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var cipherProperties: CipherProperties

    lateinit var client: OkHttpClient

    fun compare(password: String, rawString: String): Boolean {
        val hashString = toHash(rawString)
        log.debug("[Before Hash] rawString => $rawString")
        log.debug("[After Hash] compare => encoded password($password) == hashed rawString($hashString)")
        return hashString == password
    }


    fun toHash(password: String): String? {

        val postBody = """
       <soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:dat="http://www.safenet-inc.com/DataProtectionService">
          <soap:Header/>
          <soap:Body>
             <dat:HmacStringAsHex>                
                <input>$password</input>
                <context>INT_HMAC</context>
             </dat:HmacStringAsHex>
          </soap:Body>
       </soap:Envelope>       
   """.trimIndent()

        val request = Request.Builder()
                .url(cipherProperties.endpoint)
                .post(postBody.toRequestBody("application/soap+xml;charset=UTF-8".toMediaType()))
                .build()

        return client.newCall(request).execute().use { response ->
            if(response.isSuccessful)
                SimpleXMLReader.parse(response.body!!.byteStream())
            else
                null
        }
    }




    fun createTrustManager(): X509TrustManager {
        log.debug("create X509TrustManager")
        return object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String ) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String ) {
                try {
                    val trustStore = KeyStore.getInstance("JKS").apply {
                        load(cipherProperties.keystore!!.inputStream, cipherProperties.password!!.toCharArray())
                    }

                    val tms = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).run {
                        init(trustStore)
                        trustManagers[0] as X509TrustManager
                    }
                    tms.checkServerTrusted(chain, authType)
                } catch (e: Exception) {
                    when (e) {
                        is KeyStoreException,
                        is NoSuchAlgorithmException,
                        is IOException -> {
                            e.printStackTrace()
                            throw CertificateException()
                        }
                    }
                }
            }

            override fun getAcceptedIssuers(): Array<X509Certificate>? {
                return arrayOf()
            }
        }
    }

    fun customTrustManager(): X509TrustManager {
        log.debug("create CustomTrustManager")
        return object: X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }
    }


    @PostConstruct
    fun createClient() {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = (HttpLoggingInterceptor.Level.BODY)

        client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .apply {

                        val trustManager = cipherProperties.keystore?.let {
                            createTrustManager()
                        } ?: customTrustManager()

                        val sslSocketFactory = SSLContext.getInstance("TLS").run {
                            init(null, arrayOf(trustManager), null)
                            socketFactory
                        }
                        sslSocketFactory(sslSocketFactory, trustManager)

                      if(cipherProperties.keystore == null)
                          hostnameVerifier(hostnameVerifier = HostnameVerifier { _, _ -> false })

                }.build()
    }
}




class SimpleXMLReader {

    companion object {

        private const val RETURN: String = "return"

        @Throws(XMLStreamException::class)
        fun parse(input: InputStream): String? {
            val xmlFactory: XMLInputFactory = XMLInputFactory.newFactory()
            val reader: XMLEventReader = xmlFactory.createXMLEventReader(input)

            while (reader.hasNext()) {
                val event = reader.nextEvent()

                if (event.isStartElement) {
                    if ( RETURN == event.asStartElement()!!.name.localPart) {
                        return (reader.nextEvent()?.asCharacters()?.data)
                    }
                }
            }
            return null
        }
    }
}
