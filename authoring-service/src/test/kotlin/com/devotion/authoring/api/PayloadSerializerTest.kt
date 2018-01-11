package com.devotion.authoring.api

import com.devotion.authoring.ApiConfig
import com.devotion.authoring.dto.Payload
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.boot.test.json.JacksonTester
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@JsonTest
class PayloadSerializerTest {
    @Autowired
    private lateinit var json: JacksonTester<Payload<Map<String, Any>>>

    @Test
    fun `serialized Payload should be JSON object with body under data key`() {
        val body = mapOf(
                "first" to "dusan",
                "last" to "odalovic"
        )
        val responsePayload = json.write(Payload(body))
        assertThat(responsePayload).extractingJsonPathMapValue<String, Map<String, String>>("data").isEqualTo(body)
    }

    @Configuration
    class Config {
        fun apiConfig() = mock(ApiConfig::class.java)
    }
}
