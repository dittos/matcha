package org.sapzil.matcha.media.repository

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.sapzil.matcha.media.model.Media
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component
import org.springframework.util.ResourceUtils

@Component
class MediaRepository : InitializingBean {
    private val media = mutableListOf<Media>()

    override fun afterPropertiesSet() {
        val db = jacksonObjectMapper().readValue<List<Media>>(ResourceUtils.getURL("classpath:db.json"))
        media.addAll(db)
    }

    fun findById(id: String): Media {
        return media.first { it.id == id }
    }

    fun searchByName(name: String): List<Media> {
        return media.filter { it.name.contains(name, ignoreCase = true) }
    }
}