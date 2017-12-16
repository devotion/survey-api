package com.devotion.authoring.api

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import javax.validation.ValidationException

@ControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {
    private val log = LoggerFactory.getLogger(this::class.java)

    @org.springframework.web.bind.annotation.ExceptionHandler(ValidationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationException(ex: ValidationException) {
        log.error("Validation error", ex)
    }
}