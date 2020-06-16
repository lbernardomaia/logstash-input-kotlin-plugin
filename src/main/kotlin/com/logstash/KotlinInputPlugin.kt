package com.logstash

import co.elastic.logstash.api.*
import org.apache.commons.lang3.StringUtils
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.function.Consumer

@LogstashPlugin(name = "kotlin_input_plugin")
class KotlinInputPlugin @JvmOverloads constructor(
                        private val pluginId: String,
                        private val config: Configuration,
                        private val context: Context,
                        private val count : Long = config.get(EVENT_COUNT_CONFIG),
                        private val prefix : String = config.get(PREFIX_CONFIG)
) : Input {

    @Volatile private var stopped = false
    private val done = CountDownLatch(1)

    companion object {
        val EVENT_COUNT_CONFIG: PluginConfigSpec<Long> = PluginConfigSpec.numSetting("count", 3)
        val PREFIX_CONFIG: PluginConfigSpec<String> = PluginConfigSpec.stringSetting("prefix", "message")
    }

    override fun start(consumer: Consumer<Map<String, Any>>) {

        // The start method should push Map<String, Any> instances to the supplied QueueWriter
        // instance. Those will be converted to Event instances later in the Logstash event
        // processing pipeline.
        //
        // Inputs that operate on unbounded streams of data or that poll indefinitely for new
        // events should loop indefinitely until they receive a stop request. Inputs that produce
        // a finite sequence of events should loop until that sequence is exhausted or until they
        // receive a stop request, whichever comes first.
        var eventCount = 0
        try {
            while (!stopped && eventCount < count) {
                eventCount++

                consumer.accept(Collections.singletonMap<String, Any>("message",
                        prefix + " " + StringUtils.center("$eventCount of $count", 20)))
            }
        } finally {
            stopped = true
            done.countDown()
        }
    }

    override fun stop() {
        stopped = true // set flag to request cooperative stop of input
    }

    @Throws(InterruptedException::class)
    override fun awaitStop() = done.await() // blocks until input has stopped

    override fun configSchema() = listOf(EVENT_COUNT_CONFIG, PREFIX_CONFIG)

    override fun getId() = pluginId
}