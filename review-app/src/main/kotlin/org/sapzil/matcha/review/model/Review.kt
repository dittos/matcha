package org.sapzil.matcha.review.model

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.math.BigDecimal

@Document(indexName = "matcha_collection_item", type = "_doc")
data class Review(
    @Id
    val id: String,

    @Field("media_id", type = FieldType.Keyword)
    val mediaId: String,

    @Field("user_id", type = FieldType.Keyword)
    val userId: String,

    @Field(type = FieldType.Double)
    val rating: BigDecimal,

    @Field(type = FieldType.Text)
    val comment: String,

    @Field(name = "created_at", type = FieldType.Date)
    val createdAt: Long,

    @Field(name = "updated_at", type = FieldType.Date)
    val updatedAt: Long
)