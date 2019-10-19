package org.sapzil.matcha.collection

import com.google.api.client.googleapis.auth.oauth2.GooglePublicKeysManager
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.Clock
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.proc.BadJOSEException
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import org.sapzil.matcha.collection.security.FirebaseJWSKeySelector
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import reactor.core.publisher.Mono
import java.io.FileInputStream

@SpringBootApplication
class CollectionApplication : WebFluxConfigurer {
	@Bean
	fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
		http.cors()
			.and()
			.oauth2ResourceServer()
				.jwt()
					.jwtDecoder(firebaseJwtDecoder())
		return http.build()
	}

	override fun addCorsMappings(registry: CorsRegistry) {
		registry.addMapping("/**")
	}

	@Bean
	fun firebaseJwtDecoder(): ReactiveJwtDecoder {
		val serviceAccount = FileInputStream("google-credentials.json")

		val options = FirebaseOptions.Builder()
			.setCredentials(GoogleCredentials.fromStream(serviceAccount))
			.setDatabaseUrl("https://animeta-fad71.firebaseio.com")
			.build()

		val app = FirebaseApp.initializeApp(options)

		val publicKeysManager = GooglePublicKeysManager.Builder(app.options.httpTransport, GsonFactory())
			.setClock(Clock.SYSTEM)
			.setPublicCertsEncodedUrl("https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com")
			.build()
		val jwsKeySelector = FirebaseJWSKeySelector<SecurityContext>(publicKeysManager)

		val jwtProcessor = DefaultJWTProcessor<SecurityContext>()
		jwtProcessor.setJWSKeySelector(jwsKeySelector)

		// Spring Security validates the claim set independent from Nimbus
		jwtProcessor.setJWTClaimsSetVerifier { claims, context -> }

		// TODO: more validation (iss, aud)
		// https://firebase.google.com/docs/auth/admin/verify-id-tokens?hl=ko#verify_id_tokens_using_a_third-party_jwt_library

		return NimbusReactiveJwtDecoder {
			try {
				// TODO: use thread as FirebaseJWSKeySelector may block
				Mono.just(jwtProcessor.process(it, null))
			} catch (e: BadJOSEException) {
				throw JwtException("Failed to validate the token", e)
			} catch (e: JOSEException) {
				throw JwtException("Failed to validate the token", e)
			}
		}
	}
}

fun main(args: Array<String>) {
	runApplication<CollectionApplication>(*args)
}
