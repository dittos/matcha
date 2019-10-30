package org.sapzil.matcha.gateway.controller

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange
import java.math.BigDecimal
import java.time.Instant

@RestController
class MediaController {
    private val webClient = WebClient.create()

    @GetMapping("/media/{mediaId}")
    suspend fun media(@PathVariable mediaId: String, @AuthenticationPrincipal jwt: Jwt?): MediaResult = coroutineScope {
        val userId = jwt?.getClaimAsString("user_id")

        val media = async {
            webClient.get().uri("http://localhost:18002/media/$mediaId")
                .awaitExchange().awaitBody<Media>()
        }
        val item = if (userId != null) {
            async {
                try {
                    webClient.get().uri("http://localhost:18001/users/$userId/items:byMediaId/$mediaId")
                        .header("authorization", "Bearer ${jwt.tokenValue}")
                        .awaitExchange().awaitBody<Item>()
                } catch (e: NullPointerException) {
                    null
                }
            }
        } else {
            null
        }
        val reviews = async {
            webClient.get().uri("http://localhost:18003/media/$mediaId/reviews")
                .awaitExchange().awaitBody<List<Review>>()
        }
        MediaResult(
            media = media.await(),
            item = item?.await(),
            reviews = reviews.await()
        )
    }

    data class Review(
        val id: String,
        val mediaId: String,
        val userId: String,
        val rating: BigDecimal,
        val comment: String,
        val createdAt: Long,
        val updatedAt: Long
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