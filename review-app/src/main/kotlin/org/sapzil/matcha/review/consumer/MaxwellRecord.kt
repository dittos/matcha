package org.sapzil.matcha.review.consumer

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/** http://maxwells-daemon.io/dataformat/ */
@JsonIgnoreProperties(ignoreUnknown = true)
data class MaxwellRecord(
    val database: String,
    val table: String,
    val type: String,
    val ts: Long,
    val xid: Int?,
    val xoffset: Int?,
    val commit: Boolean?,
    val gtid: String,
    val primary_key: List<Any>?,
    val data: Map<String, Any>
)