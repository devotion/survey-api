package com.devotion.authoring

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.listener.KafkaListenerErrorHandler
import org.springframework.kafka.support.converter.StringJsonMessageConverter
import org.springframework.stereotype.Component
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.util.*


@SpringBootApplication
@EnableSwagger2
open class SurveyAuthoringApplication {

    @Autowired
    private lateinit var apiConfig: ApiConfig

    @Autowired
    private lateinit var kafkaConfig: KafkaConfig

    @Bean
    fun modelMapper() = ModelMapper()

    @Bean
    fun api(): Docket = Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.devotion"))
            .build()
            .apiInfo(apiInfo())

    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<String, String>) =
            KafkaTemplate(producerFactory).apply { setMessageConverter(StringJsonMessageConverter()) }

    @Bean
    fun jsonKafkaListenerContainerFactory() = ConcurrentKafkaListenerContainerFactory<String, String>().apply {
        consumerFactory = consumerFactory()
        setMessageConverter(StringJsonMessageConverter())
    }

    @Bean
    fun consumerFactory() = DefaultKafkaConsumerFactory<String, String>(consumerProperties())

    @Bean
    fun consumerProperties() = hashMapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaConfig.bootstrapAddress,
            ConsumerConfig.GROUP_ID_CONFIG to kafkaConfig.consumerGroupName,
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false,
            ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG to 15000,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java
    )

    @Bean
    fun producerFactory() = DefaultKafkaProducerFactory<String, String>(
            hashMapOf<String, Any>(
                    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaConfig.bootstrapAddress,
                    ProducerConfig.RETRIES_CONFIG to 0,
                    ProducerConfig.BATCH_SIZE_CONFIG to 16384,
                    ProducerConfig.LINGER_MS_CONFIG to 1,
                    ProducerConfig.BUFFER_MEMORY_CONFIG to 33554432,
                    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java)
    )

    @Bean
    fun kafkaListenerContainerFactory() = ConcurrentKafkaListenerContainerFactory<String, String>().apply {
        consumerFactory = consumerFactory()
    }

    @Bean
    fun validationErrorHandler(): KafkaListenerErrorHandler {
        return KafkaListenerErrorHandler { m, e -> print("this is realy bad ${e} | ${m}") }
    }

    private fun apiInfo() = ApiInfoBuilder()
            .title(apiConfig.title)
            .description(apiConfig.description)
            .contact(Contact("", "", apiConfig.contactEmail))
            .version(apiConfig.version)
            .build()

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(SurveyAuthoringApplication::class.java, *args)
        }
    }
}

@Component
@ConfigurationProperties(prefix = "api")
class ApiConfig {
    lateinit var version: String
    lateinit var title: String
    lateinit var description: String
    lateinit var contactEmail: String
}

@Component
@ConfigurationProperties(prefix = "kafka")
class KafkaConfig {
    lateinit var bootstrapAddress: String
    lateinit var consumerGroupName: String
    lateinit var answerCapturedTopic: String
    lateinit var answerStoredTopic: String
    lateinit var questionCapturedTopic: String
    lateinit var questionStoredTopic: String
    lateinit var surveyStoredTopic: String
}

annotation class NoArgConstructor

class ValidationException : RuntimeException {
    var messages: Stack<String> = Stack()

    companion object {
        private val serialVersionUID = -3685317928211708951L
    }

    constructor() {
    }

    constructor(message: String) : super(message) {
        messages.push(message)
    }

    constructor(vararg msgs: String) {
        for (msg in msgs)
            messages.push(msg)
    }
}
