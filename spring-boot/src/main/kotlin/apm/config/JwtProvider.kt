package apm.config


import apm.service.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import java.util.*

@Component
@EnableConfigurationProperties(JwtProperties::class)
class JwtProvider {

    @Autowired
    lateinit var jwtProperties: JwtProperties

    fun createJWT(user: User): String {
        return Jwts.builder()
                .setSubject(user.amId)
                .claim("name", user.name)
                .claim("nick", user.nick)
                .setIssuer(jwtProperties.issuer)
                .setIssuedAt(Date())
                .setExpiration(Date(System.currentTimeMillis() + jwtProperties.ttl.toMillis()))
                .signWith(jwtProperties.algorithm, jwtProperties.secret)
                .compact()
    }

    fun encodeJWT(token: String?): User {
        return try {
            val claims: Claims = Jwts.parser()
                    .setSigningKey(jwtProperties.secret)
                    .parseClaimsJws(token)
                    .body

             User(
                    amId = claims.subject,
                    name = claims["name"] as String,
                    nick = claims["nick"] as String
            )

        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}