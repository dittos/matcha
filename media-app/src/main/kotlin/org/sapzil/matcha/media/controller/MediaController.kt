package org.sapzil.matcha.media.controller

import org.sapzil.matcha.media.model.Media
import org.sapzil.matcha.media.repository.MediaRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class MediaController(
    private val mediaRepository: MediaRepository
) {
    @GetMapping("/media/{mediaId}")
    fun get(@PathVariable mediaId: String): Media {
        return mediaRepository.findById(mediaId)
    }

    @GetMapping("/media:batchGet")
    fun batchGet(@RequestParam mediaIds: List<String>): List<Media> {
        return mediaIds.map { mediaRepository.findById(it) }
    }

    @GetMapping("/media:search")
    fun search(@RequestParam q: String): List<Media> {
        return mediaRepository.searchByName(q)
    }
}