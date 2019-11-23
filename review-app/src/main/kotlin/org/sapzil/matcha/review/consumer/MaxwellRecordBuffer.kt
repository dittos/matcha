package org.sapzil.matcha.review.consumer

import org.springframework.messaging.Message

class MaxwellRecordBuffer {
    private val pendingRecordsByGTID = mutableMapOf<String, MutableMap<List<Any>, Message<MaxwellRecord>>>()
    private val queued = mutableListOf<List<Message<MaxwellRecord>>>()

    fun add(message: Message<MaxwellRecord>) {
        val record = message.payload
        val recordsInTx = pendingRecordsByGTID.computeIfAbsent(record.gtid) { mutableMapOf() }
        recordsInTx[record.primary_key!!] = message
        if (record.commit == true) {
            pendingRecordsByGTID.remove(record.gtid)
            queued.add(recordsInTx.values.toList())
        }
    }

    fun flush(): List<List<Message<MaxwellRecord>>> {
        val flushing = queued.toList()
        queued.clear()
        return flushing
    }
}