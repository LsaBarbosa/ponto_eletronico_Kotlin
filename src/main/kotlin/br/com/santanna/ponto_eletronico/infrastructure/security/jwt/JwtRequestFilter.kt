package br.com.santanna.ponto_eletronico.infrastructure.security.jwt

import br.com.santanna.ponto_eletronico.infrastructure.security.CustomUserDetailsService
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter


@Component
class JwtRequestFilter (
    private val customUserDetailsService: CustomUserDetailsService,
    private val jwtTokenUtil: JwtTokenUtil
) : OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val requestTokenHeader = request.getHeader("Authorization")

        var username: String? = null
        var jwtToken: String? = null

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7)
            try {
                val claims = Jwts.parser().setSigningKey(jwtTokenUtil.secretKey).parseClaimsJws(jwtToken).body
                username = claims.subject
            } catch (e: IllegalArgumentException) {
                logger.warn("Unable to get JWT Token")
            } catch (e: ExpiredJwtException) {
                logger.warn("JWT Token has expired")
            }
        } else {
            logger.warn("JWT Token does not begin with Bearer String")
        }

        if (username != null && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = customUserDetailsService.loadUserByUsername(username)

            if (jwtTokenUtil.validateToken(jwtToken!!, userDetails.username)) {
                val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, jwtToken, userDetails.authorities)
                authenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authenticationToken
            }
        }
        chain.doFilter(request, response)
    }
}