package com.devotion.capture.api

import com.devotion.capture.dto.AnswersDto
import com.devotion.capture.service.SurveyCaptureService
import com.devotion.capture.service.UserService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@RequestMapping(value = ["/capture/{surveyId}"], headers = ["Accept=application/vnd.survey-1.0+json"])
@Api(description = "Operations needed to capture submission of survey")
class SubmitController(private val captureService: SurveyCaptureService, private val userService: UserService) {
    @PostMapping("/")
    @ApiOperation(value = "Submit survey with all answers.", code = HttpServletResponse.SC_CREATED)
    fun submitSurvey(@PathVariable surveyId: String, @RequestBody @Valid surveyAnswers: AnswersDto, request: HttpServletRequest): ResponseEntity<*> {
        captureService.submitWholeSurvey(userService.resolveUser(request), surveyAnswers.answers, surveyId)
        return ResponseEntity.status(HttpStatus.CREATED).build<Any>()
    }

}
