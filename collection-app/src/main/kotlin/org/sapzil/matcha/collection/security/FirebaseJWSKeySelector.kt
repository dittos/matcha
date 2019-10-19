package org.sapzil.matcha.collection.security

import com.google.api.client.googleapis.auth.oauth2.GooglePublicKeysManager
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.proc.JWSKeySelector
import com.nimbusds.jose.proc.SecurityContext
import java.security.Key

class FirebaseJWSKeySelector<C : SecurityContext?>(
    private val publicKeysManager: GooglePublicKeysManager
) : JWSKeySelector<C> {
    override fun selectJWSKeys(header: JWSHeader?, context: C): MutableList<out Key> {
        // may block
        return publicKeysManager.publicKeys
    }
}
