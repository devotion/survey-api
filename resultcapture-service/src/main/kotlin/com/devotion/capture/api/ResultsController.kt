package com.devotion.capture.api

import com.devotion.capture.service.SurveyCaptureService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/results/{surveyId}"], headers = ["Accept=application/vnd.survey-1.0+json"])
@Api(description = "Provides operations to get response data")
class ResultsController(private val captureService: SurveyCaptureService) {

    @GetMapping("/{questionId}")
    @ApiOperation("Get all responses on single question.")
    fun getAnswersOnQuestion(@PathVariable surveyId: String, @PathVariable questionId: Int) =
            captureService.getAnswersOnQuestion(surveyId, questionId)
}
