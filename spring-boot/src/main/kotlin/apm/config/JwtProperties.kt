package apm.config

import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.time.Duration

@ConstructorBinding
@ConfigurationProperties(prefix = "spring.jwt")
data class JwtProperties(
        val secret: String,
        val issuer: String,
        val ttl: Duration,
        val algorithm: SignatureAlgorithm = SignatureAlgorithm.HS256
)
