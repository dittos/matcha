package org.sapzil.matcha.review.consumer

import org.elasticsearch.action.delete.DeleteRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.index.VersionType
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message

@EnableBinding(MaxwellSink::class)
class MaxwellStreamProcessor(
    private val es: ElasticsearchRestTemplate
) {
    private val buffer = MaxwellRecordBuffer()

    @StreamListener(MaxwellSink.INPUT)
    fun handle(message: Message<MaxwellRecord>) {
        // TODO: deduplication with gtid
        buffer.add(message)

        tryFlush()
    }

    private fun tryFlush() {
        val messages = buffer.flush().flatten()
        for (message in messages) {
            val record = message.payload
            println(record)

            if (record.table != "item")
                continue

            val indexName = "${record.database}_${record.table}"

            try {
                if (record.type == "delete") {
                    es.client.delete(DeleteRequest(indexName, "_doc", record.data["id"].toString()),
                        RequestOptions.DEFAULT)
                } else {
                    es.client.index(IndexRequest(indexName, "_doc", record.data["id"].toString())
                        .source(record.data)
                        .version(record.gtid.split(":").last().toLong())
                        .versionType(VersionType.EXTERNAL), RequestOptions.DEFAULT)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Ack phase
        // TODO: batch ack?
        for (message in messages) {
            val acknowledgment = message.headers.get(KafkaHeaders.ACKNOWLEDGMENT, Acknowledgment::class.java)
            acknowledgment?.acknowledge()
        }
    }
}
