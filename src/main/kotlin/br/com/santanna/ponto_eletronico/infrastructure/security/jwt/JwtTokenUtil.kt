package br.com.santanna.ponto_eletronico.infrastructure.security.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenUtil {
    @Value("\${jwt.secret}")
     lateinit var secretKey: String

    @Value("\${jwt.expiration}")
    private var expiration: Long = 0

    fun generateToken(userDetails: UserDetails, userId: UUID, userRole: String): String {
        val claims = Jwts.claims().setSubject(userDetails.username)
        claims["id"] = userId.toString()
        claims["role"] = userRole
        val now = Date()
        val validity = Date(now.time + expiration * 1000)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
    }

    fun getClaimsFromToken(token: String): Claims {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body
    }

    fun getUserIdFromToken(token: String): UUID {
        val claims = getClaimsFromToken(token)
        return UUID.fromString(claims["id"].toString())
    }

    fun getRoleFromToken(token: String): String {
        val claims = getClaimsFromToken(token)
        return claims["role"].toString()
    }

    fun isTokenExpired(token: String): Boolean {
        return getClaimsFromToken(token).expiration.before(Date())
    }

    fun validateToken(token: String, username: String): Boolean {
        val tokenUsername = getClaimsFromToken(token).subject
        return username == tokenUsername && !isTokenExpired(token)
    }

}