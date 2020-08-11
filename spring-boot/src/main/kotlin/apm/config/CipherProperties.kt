package apm.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.core.io.Resource

@ConstructorBinding
@ConfigurationProperties(prefix = "spring.cipher")
data class CipherProperties(
        val endpoint: String,
        val keystore: Resource?,
        val password: String?
)

