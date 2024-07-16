package br.com.santanna.ponto_eletronico.infrastructure.security.configsec

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenUtil {  @Value("\${jwt.secret}")
private lateinit var secret: String

    @Value("\${jwt.expiration}")
    private var expiration: Long = 0

    fun generateToken(cpf: String, id: UUID): String {
        val claims = mutableMapOf<String, Any>()
        claims["id"] = id
        return doGenerateToken(claims, cpf)
    }

    private fun doGenerateToken(claims: Map<String, Any>, subject: String): String {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expiration * 1000))
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact()
    }

    fun getClaimsFromToken(token: String): Claims {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body
    }

    fun isTokenExpired(token: String): Boolean {
        return getClaimsFromToken(token).expiration.before(Date())
    }

    fun validateToken(token: String, cpf: String): Boolean {
        val username = getClaimsFromToken(token).subject
        return username == cpf && !isTokenExpired(token)
    }
}