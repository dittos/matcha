package org.sapzil.matcha.security.firebase

import com.google.api.client.googleapis.auth.oauth2.GooglePublicKeysManager
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.Clock
import com.google.firebase.FirebaseApp
import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.proc.BadJOSEException
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import reactor.core.publisher.Mono

@Configuration(proxyBeanMethods = false)
class FirebaseAuthConfiguration {
    @Bean
    fun firebaseJwtDecoder(firebaseApp: FirebaseApp): ReactiveJwtDecoder {
        val publicKeysManager = GooglePublicKeysManager.Builder(firebaseApp.options.httpTransport, GsonFactory())
            .setClock(Clock.SYSTEM)
            .setPublicCertsEncodedUrl("https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com")
            .build()
        val jwsKeySelector = FirebaseJWSKeySelector<SecurityContext>(publicKeysManager)

        val jwtProcessor = DefaultJWTProcessor<SecurityContext>()
        jwtProcessor.jwsKeySelector = jwsKeySelector

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