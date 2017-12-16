package com.draganlj.survey.capture;

import com.draganlj.survey.capture.event.CaptureResultCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashMap;
import java.util.Map;


@SpringBootApplication
@EnableSwagger2
public class SurveyCaptureApplication {

    @Value("${1kafka.servers}")
    private String bootstrapAddress;


    @Value("${api.version}")
    private String apiVersion;

    public static void main(String[] args) {
        SpringApplication.run(SurveyCaptureApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public Docket api() {
        //noinspection Guava
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.draganlj.survey.capture"))
                .build().apiInfo(metadata());
    }


    @Bean
    public ProducerFactory<String, CaptureResultCreatedEvent> producerFactory() {
        Map<String, Object> configProps = getConfigProps();
        return new DefaultKafkaProducerFactory<>(configProps);
    }


    @Bean
    public KafkaTemplate<String, CaptureResultCreatedEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CaptureResultCreatedEvent> filterPersistedFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CaptureResultCreatedEvent> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setRecordFilterStrategy(
                record -> record.value().isPersisted());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CaptureResultCreatedEvent> filterNonPersistedFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CaptureResultCreatedEvent> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setRecordFilterStrategy(
                record -> !record.value().isPersisted());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CaptureResultCreatedEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CaptureResultCreatedEvent> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, CaptureResultCreatedEvent> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                getConfigProps(),
                new StringDeserializer(),
                new JsonDeserializer<>(CaptureResultCreatedEvent.class));
    }

    private Map<String, Object> getConfigProps() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "testGrp1");
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        configProps.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        configProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");

        return configProps;
    }

    private ApiInfo metadata() {
        return new ApiInfoBuilder()
                .title("Survey submission API")
                .description("Operations on this API allows you to capture results of survey submission.")
                .contact(new Contact("Dragan Ljubojevic", "", "dragan.ljubojevic@gmail.com"))
                .version(apiVersion)
                .build();
    }

}
