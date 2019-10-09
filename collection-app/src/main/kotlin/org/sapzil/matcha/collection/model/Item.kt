package org.sapzil.matcha.collection.model

import java.math.BigDecimal
import java.time.Instant
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Item(
    @Id
    @GeneratedValue
    var id: Long? = null,

    var userId: String,

    var mediaId: String,

    var rating: BigDecimal?,

    var comment: String?,

    var createdAt: Instant,

    var updatedAt: Instant
)
