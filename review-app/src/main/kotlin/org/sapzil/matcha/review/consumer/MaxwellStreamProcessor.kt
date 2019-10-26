package org.sapzil.matcha.review.consumer

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.elasticsearch.action.delete.DeleteRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.index.VersionType
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.connection.stream.StreamOffset
import org.springframework.data.redis.connection.stream.StreamReadOptions
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Component
class MaxwellStreamProcessor(
    private val redis: ReactiveStringRedisTemplate,
    private val es: ElasticsearchRestTemplate
) {
    val executor = Executors.newSingleThreadExecutor()

    @PostConstruct
    fun start() {
        executor.submit(this::run)
    }

    @PreDestroy
    fun destroy() {
        executor.shutdown()
        executor.awaitTermination(30, TimeUnit.SECONDS)
    }

    private fun run() {
        println("Started MaxwellStreamProcessor")
        val stream = "maxwell"
        var offset = StreamOffset.latest(stream) // TODO: save/restore checkpoint
        val mapper = jacksonObjectMapper()
        val buffer = MaxwellRecordBuffer()
        while (true) {
            val result = redis.opsForStream<String, String>()
                .read(StreamReadOptions.empty().block(Duration.ofSeconds(30)), offset)
                .toIterable()
            var lastRedisRecord: MapRecord<String, String, String>? = null
            for (redisRecord in result) {
                // TODO: deduplication with gtid
                buffer.add(mapper.readValue(redisRecord.value["message"]!!))
                lastRedisRecord = redisRecord
            }
            if (lastRedisRecord != null) {
                offset = StreamOffset.from(lastRedisRecord)
            }
            for (record in buffer.flush().flatten()) {
                println(record)

                if (record.table != "item")
                    continue

                val indexName = "${record.database}_${record.table}"

                try {
                    if (record.type == "delete") {
                        es.client.delete(DeleteRequest(indexName, record.table, record.data["id"].toString()),
                            RequestOptions.DEFAULT)
                    } else {
                        es.client.index(IndexRequest(indexName, record.table, record.data["id"].toString())
                            .source(record.data)
                            .version(record.gtid.split(":").last().toLong())
                            .versionType(VersionType.EXTERNAL), RequestOptions.DEFAULT)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}