package com.devotion.capture

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.support.converter.StringJsonMessageConverter
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.util.*


@SpringBootApplication
@EnableSwagger2
open class SurveyCaptureApplication {

    @Value("\${kafka.bootstrap-servers}")
    private lateinit var bootstrapAddress: String

    @Value("\${kafka.consumer-group}")
    private lateinit var consumerGroupName: String

    @Value("\${api.version}")
    private lateinit var apiVersion: String

    @Bean
    open fun modelMapper() = ModelMapper()

    @Bean
    open fun api(): Docket = Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.devotion"))
            .build()
            .apiInfo(apiInfo())

    @Bean
    open fun kafkaTemplate(producerFactory: ProducerFactory<String, String>): KafkaTemplate<String, String> {
        val kafkaTemplate = KafkaTemplate(producerFactory)
        kafkaTemplate.setMessageConverter(StringJsonMessageConverter())
        return kafkaTemplate
    }

    @Bean
    open fun jsonKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory()
        factory.setMessageConverter(StringJsonMessageConverter())
        return factory
    }

    @Bean
    open fun consumerFactory() = DefaultKafkaConsumerFactory<String, String>(consumerProperties())

    @Bean
    open fun consumerProperties() = HashMap<String, Any>().apply {
        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress)
        put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupName)
        put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false)
        put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000)
        put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
        put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
    }

    @Bean
    open fun producerFactory() = DefaultKafkaProducerFactory<String, String>(
            HashMap<String, Any>().apply {
                put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress)
                put(ProducerConfig.RETRIES_CONFIG, 0)
                put(ProducerConfig.BATCH_SIZE_CONFIG, 16384)
                put(ProducerConfig.LINGER_MS_CONFIG, 1)
                put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432)
                put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
                put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
            }
    )

    @Bean
    open fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory()
        return factory
    }

    private fun apiInfo() = ApiInfoBuilder()
            .title("Survey submission API")
            .description("Operations on this API allows you to capture results of survey submission.")
            .contact(Contact("Dragan Ljubojevic", "", "dragan.ljubojevic@gmail.com"))
            .version(apiVersion)
            .build()

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(SurveyCaptureApplication::class.java, *args)
        }
    }
}
