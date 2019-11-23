package org.sapzil.matcha.review.consumer

import org.springframework.cloud.stream.annotation.Input
import org.springframework.messaging.SubscribableChannel

interface MaxwellSink {
    companion object {
        const val INPUT = "input"
    }

    @Input(INPUT)
    fun input(): SubscribableChannel
}
