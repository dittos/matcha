package org.sapzil.matcha.media.model

class Movie(
    override val id: String,

    override val name: String,

    override val image: String?,

    override val imageBlurhash: String?
) : Media