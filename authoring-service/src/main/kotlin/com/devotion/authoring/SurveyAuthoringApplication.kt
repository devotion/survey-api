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
open class  SurveyAuthoringApplication {

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
    fun jsonKafkaListenerContainerFactory() =
            ConcurrentKafkaListenerContainerFactory<String, String>().apply {
                consumerFactory = consumerFactory()
                setMessageConverter(StringJsonMessageConverter())
            }

    @Bean
    fun consumerFactory() = DefaultKafkaConsumerFactory<String, String>(consumerProperties())

    @Bean
    fun consumerProperties() = HashMap<String, Any>().apply {
        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.bootstrapAddress)
        put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfig.consumerGroupName)
        put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false)
        put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000)
        put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
        put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
    }

    @Bean
    fun producerFactory() = DefaultKafkaProducerFactory<String, String>(
            HashMap<String, Any>().apply {
                put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.bootstrapAddress)
                put(ProducerConfig.RETRIES_CONFIG, 0)
                put(ProducerConfig.BATCH_SIZE_CONFIG, 16384)
                put(ProducerConfig.LINGER_MS_CONFIG, 1)
                put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432)
                put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
                put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
            }
    )

    @Bean
    fun kafkaListenerContainerFactory() =
            ConcurrentKafkaListenerContainerFactory<String, String>().apply { consumerFactory = consumerFactory() }

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

class ValidationException(message: String) : RuntimeException(message) {
    companion object {
        private val serialVersionUID = -3685317928211708951L
    }
}

