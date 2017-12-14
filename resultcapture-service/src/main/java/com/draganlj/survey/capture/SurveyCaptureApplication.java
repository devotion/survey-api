package com.draganlj.survey.capture;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication
@EnableSwagger2
public class SurveyCaptureApplication {

    public static void main(String[] args) {
        SpringApplication.run(SurveyCaptureApplication.class, args);
    }

    @Value("${api.version}")
    private String apiVersion;

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

    private ApiInfo metadata() {
        return new ApiInfoBuilder()
                .title("Survey submission API")
                .description("Operations on this API allows you to capture results of survey submission.")
                .contact(new Contact("Dragan Ljubojevic", "", "dragan.ljubojevic@gmail.com"))
                .version(apiVersion)
                .build();
    }

}
