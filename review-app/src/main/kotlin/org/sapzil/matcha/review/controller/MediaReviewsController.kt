package org.sapzil.matcha.review.controller

import org.sapzil.matcha.review.model.Review
import org.sapzil.matcha.review.repository.ReviewRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class MediaReviewsController(
    private val reviewRepository: ReviewRepository
) {
    @GetMapping("/media/{mediaId}/reviews")
    fun list(@PathVariable mediaId: String): Mono<List<Review>> {
        return reviewRepository.findByMediaId(mediaId).collectList()
    }
}