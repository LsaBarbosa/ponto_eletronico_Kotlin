package br.com.santanna.ponto_eletronico.infrastructure.security

import br.com.santanna.ponto_eletronico.infrastructure.security.jwt.JwtAuthenticationEntryPoint
import br.com.santanna.ponto_eletronico.infrastructure.security.jwt.JwtRequestFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
@EnableWebSecurity
class WebSecurityConfig (
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,

    private val jwtRequestFilter: JwtRequestFilter
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf -> csrf.disable() }
            .cors { }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers(
                        "/authentication/login",
                        "/employee/password/reset",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/swagger-ui.html",
                        "/webjars/**"

                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .exceptionHandling { exceptionHandling ->
                exceptionHandling
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            }
            .sessionManagement { sessionManagement ->
                sessionManagement
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.allowedOrigins = listOf("https://kronos-cw4x.onrender.com") // Substitua pela URL do seu front-end
        config.allowedHeaders = listOf("*")
        config.allowedMethods = listOf("*")
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)

    }
}