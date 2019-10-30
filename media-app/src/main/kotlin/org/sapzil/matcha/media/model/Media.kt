package org.sapzil.matcha.media.model

import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
interface Media {
    val id: String
    val name: String
    val image: String?
    val imageBlurhash: String?
}