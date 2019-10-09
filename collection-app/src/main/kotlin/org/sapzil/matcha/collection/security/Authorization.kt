package org.sapzil.matcha.collection.security

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.oauth2.jwt.Jwt

fun Jwt.checkCurrentUserIdOrThrow(expectedUserId: String) {
    if (getClaimAsString("userId") != expectedUserId) {
        throw AccessDeniedException("")
    }
}