package org.sapzil.matcha.userprofile.controller

import org.sapzil.matcha.collection.security.checkCurrentUserIdOrThrow
import org.sapzil.matcha.userprofile.model.UserProfile
import org.sapzil.matcha.userprofile.repository.UserProfileRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserProfileController(
    private val userProfileRepository: UserProfileRepository
) {
    @GetMapping("/users/{userId}/profile")
    fun get(@PathVariable userId: String): UserProfile {
        return userProfileRepository.findByIdOrNull(userId) ?: UserProfile(
            id = userId,
            nickname = ""
        )
    }

    @PostMapping("/users/{userId}/profile")
    fun update(@PathVariable userId: String, @RequestBody request: UserProfile, @AuthenticationPrincipal jwt: Jwt): UserProfile {
        jwt.checkCurrentUserIdOrThrow(userId)

        val userProfile = userProfileRepository.findByIdOrNull(userId) ?: UserProfile(
            id = userId,
            nickname = ""
        )
        userProfile.nickname = request.nickname
        userProfile.imageUrl = request.imageUrl
        userProfile.imageWidth = request.imageWidth
        userProfile.imageHeight = request.imageHeight
        userProfileRepository.save(userProfile)

        return userProfile
    }
}
