package br.com.santanna.ponto_eletronico.infrastructure.util

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean

@Configuration
class MessageSourceConfig {
    @Bean
    fun messageSource(): MessageSource {
        val messageSource = ResourceBundleMessageSource()
        messageSource.setBasename("messages")
        messageSource.setDefaultEncoding("UTF-8")
        return messageSource
    }

    @Bean
    fun validator(): LocalValidatorFactoryBean {
        val bean = LocalValidatorFactoryBean()
        bean.setValidationMessageSource(messageSource())
        return bean
    }
}