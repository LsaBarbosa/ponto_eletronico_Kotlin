package br.com.santanna.ponto_eletronico.infrastructure.security.configsec

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

        var cpf: String? = null
        var jwtToken: String? = null

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7)
            try {
                cpf = jwtTokenUtil.getClaimsFromToken(jwtToken).subject
            } catch (e: Exception) {
                logger.error("Unable to get JWT Token or JWT Token has expired")
            }
        } else {
            logger.warn("JWT Token does not begin with Bearer String")
        }

        if (cpf != null && SecurityContextHolder.getContext().authentication == null) {
            if (jwtTokenUtil.validateToken(jwtToken!!, cpf)) {
                val authentication = UsernamePasswordAuthenticationToken(
                    customUserDetailsService.loadUserByUsername(cpf), null, customUserDetailsService.loadUserByUsername(cpf).authorities
                )
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            }
        }
        chain.doFilter(request, response)
    }
}