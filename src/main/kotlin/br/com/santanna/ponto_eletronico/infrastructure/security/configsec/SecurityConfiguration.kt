package br.com.santanna.ponto_eletronico.infrastructure.security.configsec

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

private const val _EMPRESA = "/empresa"
private const val _COLABORADOR = "/colaborador"

private const val _PONTO = "/ponto"
private const val ADMIN = "ADMIN"
private const val MANAGER = "MANAGER"
private const val USER = "USER"

@Configuration
@EnableWebSecurity
class SecurityConfigurations {

    @Autowired
    private lateinit var securityFilter: SecurityFilter

    @Bean
    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity.csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { authorize ->
//                authorize.requestMatchers(HttpMethod.GET, "$_EMPRESA/busca-cnpj").hasRole(ADMIN)
//                authorize.requestMatchers(HttpMethod.GET, "$_EMPRESA/busca-nome-empresa").hasRole(ADMIN)
//                authorize.requestMatchers(HttpMethod.GET, _EMPRESA).hasRole(ADMIN)
//                authorize.requestMatchers(HttpMethod.POST, _EMPRESA).permitAll()
//                authorize.requestMatchers(HttpMethod.PUT, _EMPRESA).hasRole(ADMIN)
//                authorize.requestMatchers(HttpMethod.DELETE,_EMPRESA).hasRole(ADMIN)
//
//                authorize.requestMatchers(HttpMethod.POST, "$_COLABORADOR/criar-colaborador").permitAll()
//                authorize.requestMatchers(HttpMethod.POST, "$_COLABORADOR/login").permitAll()
//                authorize.requestMatchers(HttpMethod.GET, "$_COLABORADOR/busca").hasRole(USER)
//                authorize.requestMatchers(HttpMethod.GET, _COLABORADOR).hasRole(MANAGER)
//                authorize.requestMatchers(HttpMethod.PUT, _COLABORADOR).hasRole(MANAGER)
//                authorize.requestMatchers(HttpMethod.DELETE, _COLABORADOR).hasRole(MANAGER)
//
//                authorize.requestMatchers(HttpMethod.PUT, _PONTO).hasRole(MANAGER)
//                authorize.requestMatchers(HttpMethod.PUT, _PONTO).hasRole(MANAGER)
//                authorize.requestMatchers(HttpMethod.DELETE, _PONTO).hasRole(MANAGER)
                authorize.anyRequest().permitAll()
            }
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter::class.java)

        return httpSecurity.build()
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager =
        authenticationConfiguration.authenticationManager

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun corsConfigurer(): WebMvcConfigurer = object : WebMvcConfigurer {
        override fun addCorsMappings(registry: CorsRegistry) {
            registry.addMapping("/**")
                .allowedOrigins("https://ponto-eletronico-nova-alianca.vercel.app")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
        }
    }
}