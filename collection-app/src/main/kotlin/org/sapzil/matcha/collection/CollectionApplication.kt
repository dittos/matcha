package org.sapzil.matcha.collection

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.sapzil.matcha.security.firebase.FirebaseAuthConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import java.io.FileInputStream

@SpringBootApplication
@Import(FirebaseAuthConfiguration::class)
class CollectionApplication {
	@Bean
	fun corsConfigurationSource(): CorsConfigurationSource {
		return UrlBasedCorsConfigurationSource().apply {
			registerCorsConfiguration("/**", CorsConfiguration().applyPermitDefaultValues())
		}
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
	runApplication<CollectionApplication>(*args)
}
