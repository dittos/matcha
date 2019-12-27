package org.sapzil.matcha.gateway.controller

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.Instant
import java.util.Optional

@RestController
class MediaController(webClientBuilder: WebClient.Builder) {
    private val webClient = webClientBuilder.build()

    @GetMapping("/media/{mediaId}")
    fun media(@PathVariable mediaId: String, @AuthenticationPrincipal jwt: Jwt?): Mono<MediaResult> {
        val userId = jwt?.getClaimAsString("user_id")

        val media = webClient.get().uri("http://localhost:18002/media/$mediaId")
            .retrieve()
            .bodyToMono<Media>()

        val item = if (userId != null) {
            webClient.get().uri("http://localhost:18001/users/$userId/items:byMediaId/$mediaId")
                .header("authorization", "Bearer ${jwt.tokenValue}")
                .retrieve()
                .bodyToMono<Item>()
                .onErrorResume(NullPointerException::class.java) { Mono.empty() }
        } else {
            Mono.empty()
        }

        val reviews = webClient.get().uri("http://localhost:18003/media/$mediaId/reviews")
            .retrieve().bodyToMono<List<Review>>()
            .flatMapIterable { it }
            .flatMap { review ->
                webClient.get().uri("http://localhost:18004/users/${review.userId}/profile")
                    .retrieve().bodyToMono<UserProfile>()
                    .map { userProfile -> review.copy(userProfile = userProfile) }
            }
            .collectList()

        return Mono.zip(media.emptyToOptional(), item.emptyToOptional(), reviews).map {
            MediaResult(
                media = it.t1.orElse(null),
                item = it.t2.orElse(null),
                reviews = it.t3
            )
        }
    }

    fun <T> Mono<T>.emptyToOptional() =
        map { Optional.of(it) }.defaultIfEmpty(Optional.empty())

    data class Review(
        val id: String,
        val mediaId: String,
        val userId: String,
        val rating: BigDecimal,
        val comment: String,
        val createdAt: Long,
        val updatedAt: Long,
        val userProfile: UserProfile?
    )

    data class UserProfile(
        val id: String,
        val nickname: String,
        val imageUrl: String?,
        val imageWidth: Int?,
        val imageHeight: Int?
    )

    data class Media(
        val id: String,
        val name: String,
        val image: String?,
        val imageBlurhash: String?
    )

    data class Item(
        var id: Long,
        var userId: String,
        var mediaId: String,
        var rating: BigDecimal?,
        var comment: String?,
        var createdAt: Instant,
        var updatedAt: Instant
    )

    data class MediaResult(
        val media: Media,
        val item: Item?,
        val reviews: List<Review>
    )
}