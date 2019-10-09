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

@RestController
@RequestMapping("/users/{userId}/items")
class UserItemsController(
    private val itemRepository: ItemRepository
) {
    @GetMapping
    fun list(@PathVariable userId: String, @AuthenticationPrincipal jwt: Jwt): List<Item> {
        jwt.checkCurrentUserIdOrThrow(userId)
        return itemRepository.findAll { root, query, criteriaBuilder ->
            criteriaBuilder.run { equal(root[Item_.userId], userId) }
        }
    }

    @PostMapping
    fun create(@PathVariable userId: String, @RequestBody request: Item, @AuthenticationPrincipal jwt: Jwt): Item {
        jwt.checkCurrentUserIdOrThrow(userId)
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
