package org.sapzil.matcha.review

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.support.DefaultConversionService
import org.springframework.data.elasticsearch.config.ElasticsearchConfigurationSupport
import org.springframework.data.elasticsearch.core.ElasticsearchEntityMapper
import org.springframework.data.elasticsearch.core.EntityMapper

@Configuration
class ElasticsearchConfiguration : ElasticsearchConfigurationSupport() {
    @Bean
    override fun entityMapper(): EntityMapper {
        val entityMapper = ElasticsearchEntityMapper(
            elasticsearchMappingContext(),
            DefaultConversionService()
        )
        entityMapper.setConversions(elasticsearchCustomConversions())
        return entityMapper
    }
}
