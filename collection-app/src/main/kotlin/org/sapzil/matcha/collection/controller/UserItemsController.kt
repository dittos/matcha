package org.sapzil.matcha.collection.controller

import org.sapzil.matcha.collection.model.Item
import org.sapzil.matcha.collection.model.Item_
import org.sapzil.matcha.collection.repository.ItemRepository
import org.sapzil.matcha.collection.security.checkCurrentUserIdOrThrow
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.Optional

@RestController
@RequestMapping
class UserItemsController(
    private val itemRepository: ItemRepository
) {
    @GetMapping("/users/{userId}/items")
    fun list(@PathVariable userId: String, @AuthenticationPrincipal jwt: Jwt): List<Item> {
        jwt.checkCurrentUserIdOrThrow(userId)
        return itemRepository.findAll { root, query, criteriaBuilder ->
            criteriaBuilder.run { equal(root[Item_.userId], userId) }
        }
    }

    @GetMapping("/users/{userId}/items:byMediaId/{mediaId}")
    fun getByMediaId(@PathVariable userId: String, @PathVariable mediaId: String, @AuthenticationPrincipal jwt: Jwt): Optional<Item> {
        jwt.checkCurrentUserIdOrThrow(userId)
        return itemRepository.findOne { root, query, criteriaBuilder ->
            criteriaBuilder.run {
                and(
                    equal(root[Item_.userId], userId),
                    equal(root[Item_.mediaId], mediaId)
                )
            }
        }
    }

    @PostMapping("/users/{userId}/items")
    fun create(@PathVariable userId: String, @RequestBody request: Item, @AuthenticationPrincipal jwt: Jwt): Item {
        jwt.checkCurrentUserIdOrThrow(userId)
        // TODO: check uniqueness of (userId, mediaId)
        return itemRepository.save(Item(
            userId = userId,
            mediaId = request.mediaId,
            rating = request.rating,
            comment = request.comment,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        ))
    }
}
