package com.devotion.authoring

import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2


@SpringBootApplication
@EnableSwagger2
open class SurveyAuthoringApplication {

    @Value("\${api.version}")
    private lateinit var apiVersion: String

    @Bean
    open fun modelMapper() = ModelMapper()

    @Bean
    open fun api(): Docket = Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.draganlj.survey.authoring"))
            .build().apiInfo(metadata())

    private fun metadata(): ApiInfo {
        return ApiInfoBuilder()
                .title("Survey authoring API")
                .description("Operations on this API allows you to create syrvey with answers and questions.")
                .contact(Contact("Dragan Ljubojevic", "", "dragan.ljubojevic@gmail.com"))
                .version(apiVersion)
                .build()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(SurveyAuthoringApplication::class.java, *args)
        }
    }
}
