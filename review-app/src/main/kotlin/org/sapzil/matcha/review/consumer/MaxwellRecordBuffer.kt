package org.sapzil.matcha.review.consumer

class MaxwellRecordBuffer {
    private val pendingRecordsByGTID = mutableMapOf<String, MutableMap<List<Any>, MaxwellRecord>>()
    private val queued = mutableListOf<List<MaxwellRecord>>()

    fun add(record: MaxwellRecord) {
        val recordsInTx = pendingRecordsByGTID.computeIfAbsent(record.gtid) { mutableMapOf() }
        recordsInTx[record.primary_key!!] = record
        if (record.commit == true) {
            pendingRecordsByGTID.remove(record.gtid)
            queued.add(recordsInTx.values.toList())
        }
    }

    fun flush(): List<List<MaxwellRecord>> {
        val flushing = queued.toList()
        queued.clear()
        return flushing
    }
}