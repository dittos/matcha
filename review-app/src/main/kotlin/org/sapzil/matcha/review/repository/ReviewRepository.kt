package org.sapzil.matcha.review.repository

import org.sapzil.matcha.review.model.Review
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import reactor.core.publisher.Flux

interface ReviewRepository : ReactiveSortingRepository<Review, String> {
    fun findByMediaId(mediaId: String): Flux<Review>
}