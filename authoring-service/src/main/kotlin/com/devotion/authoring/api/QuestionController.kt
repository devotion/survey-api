package com.devotion.authoring.api

import com.devotion.authoring.dto.QuestionAll
import com.devotion.authoring.dto.QuestionIdAndText
import com.devotion.authoring.dto.QuestionText
import com.devotion.authoring.service.SurveyAuthoringService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@RequestMapping(value = ["/questions/{surveyId}"], headers = ["Accept=application/vnd.survey-1.0+json"])
@Api(description = "Basic operations on questions")
class QuestionController(private val service: SurveyAuthoringService) {

    @PostMapping("/")
    @ApiOperation(value = "Add question to survey", code = HttpServletResponse.SC_CREATED)
    fun addQuestion(@PathVariable surveyId: String, @Valid @RequestBody question: QuestionText): ResponseEntity<*> {
        service.addQuestion(surveyId, question)
        return ResponseEntity.accepted().build<Any>()
    }

    @PutMapping("/{questionId}")
    @ApiOperation(value = "Update existing question in survey", code = HttpServletResponse.SC_NO_CONTENT)
    fun updateQuestion(@PathVariable surveyId: String, @PathVariable questionId: String, @Valid @RequestBody question: QuestionText): ResponseEntity<*> {
        service.updateQuestion(surveyId, questionId, question)
        return ResponseEntity.noContent().build<Any>()
    }

    @DeleteMapping("/{questionId}")
    @ApiOperation("Remove question from survey")
    fun deleteQuestion(@PathVariable surveyId: String, @PathVariable questionId: String): ResponseEntity<*> {
        service.deleteQuestion(surveyId, questionId)
        return ResponseEntity.ok().build<Any>()
    }

    @GetMapping("/{questionId}")
    @ApiOperation("Return single question with answers")
    fun getQuestionWithAnswers(@PathVariable surveyId: String, @PathVariable questionId: String): QuestionAll {
        return service.getQuestion(surveyId, questionId, true)
    }

    @GetMapping("/")
    @ApiOperation("Return all questions from survey with no answers")
    fun getAllQuestions(@PathVariable surveyId: String): List<QuestionIdAndText> {
        return service.getAllQuestions(surveyId)
    }
}
