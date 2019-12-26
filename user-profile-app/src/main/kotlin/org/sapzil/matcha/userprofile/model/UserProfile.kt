package org.sapzil.matcha.userprofile.model

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class UserProfile(
    @Id
    var id: String? = null,

    var nickname: String,

    var imageUrl: String? = null,

    var imageWidth: Int? = null,

    var imageHeight: Int? = null
)
