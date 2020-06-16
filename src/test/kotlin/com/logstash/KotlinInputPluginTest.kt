package com.logstash

import co.elastic.logstash.api.Configuration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.logstash.plugins.ConfigurationImpl
import org.logstash.plugins.ContextImpl
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import java.util.function.Consumer


class KotlinInputPluginTest {

    @Test
    fun `When eventCount is 5, Then event processed should be equals to 5`() {
        val prefix = "This is message"
        val eventCount: Long = 5

        val configValues = mapOf(
                KotlinInputPlugin.PREFIX_CONFIG.name() to prefix,
                KotlinInputPlugin.EVENT_COUNT_CONFIG.name() to eventCount
        )

        val config: Configuration = ConfigurationImpl(configValues)

        val input = KotlinInputPlugin("test-id", config, ContextImpl(null, null))

        val testConsumer = TestConsumer

        input.start(testConsumer)

        val events: List<Map<String, Any>> = testConsumer.getEvents()

        assertEquals(eventCount, events.size.toLong())
    }

    private object TestConsumer : Consumer<Map<String, Any>> {
        private val events: MutableList<Map<String, Any>> = ArrayList()
        private val lock = ReentrantLock()

        override fun accept(event: Map<String, Any>) {
            lock.lock()
            try {
                events.add(event)
            } finally {
                lock.unlock()
            }
        }

        fun getEvents(): List<Map<String, Any>> = events
    }
}