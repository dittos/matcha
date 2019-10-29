package org.sapzil.matcha.gateway

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.sapzil.matcha.security.firebase.FirebaseAuthConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import java.io.FileInputStream

@SpringBootApplication
@Import(FirebaseAuthConfiguration::class)
class GatewayApplication {
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", CorsConfiguration().applyPermitDefaultValues())
        }
    }

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity, jwtDecoder: ReactiveJwtDecoder): SecurityWebFilterChain {
        return http.authorizeExchange()
            .anyExchange()
            .permitAll()
            .and()
            .oauth2ResourceServer()
                .jwt()
                    .jwtDecoder(jwtDecoder)
                .and()
            .and().build()
    }

    @Bean
    fun firebaseApp(): FirebaseApp {
        val serviceAccount = FileInputStream("google-credentials.json")

        val options = FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()

        return FirebaseApp.initializeApp(options)
    }
}

fun main(args: Array<String>) {
    runApplication<GatewayApplication>(*args)
}
