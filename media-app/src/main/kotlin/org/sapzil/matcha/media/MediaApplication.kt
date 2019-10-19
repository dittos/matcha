package org.sapzil.matcha.media

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer

@SpringBootApplication
class MediaApplication : WebFluxConfigurer {
	override fun addCorsMappings(registry: CorsRegistry) {
		registry.addMapping("/**")
	}
}

fun main(args: Array<String>) {
	runApplication<MediaApplication>(*args)
}
