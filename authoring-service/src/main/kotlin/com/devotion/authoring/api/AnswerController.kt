package com.devotion.authoring.api

import com.devotion.authoring.dto.AnswerIdAndText
import com.devotion.authoring.dto.AnswerText
import com.devotion.authoring.service.SurveyAuthoringService
import io.swagger.annotations.ApiOperation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@RequestMapping(value = ["/answers/{surveyId}"], headers = ["Accept=application/vnd.survey-1.0+json"])
class AnswerController(private val service: SurveyAuthoringService) {

    @PostMapping("/{questionId}")
    @ApiOperation(value = "Add new answer to the question", code = HttpServletResponse.SC_CREATED)
    fun addAnswer(@PathVariable surveyId: String, @PathVariable questionId: String, @Valid @RequestBody answer: AnswerText): ResponseEntity<*> {
        service.addAnswer(surveyId, questionId, answer)
        return ResponseEntity.accepted().build<Any>()
    }

    @PutMapping("/{answerId}")
    @ApiOperation(value = "Update existing answer", code = HttpServletResponse.SC_NO_CONTENT)
    fun updateAnswer(@PathVariable surveyId: String, @PathVariable answerId: String, @Valid @RequestBody answer: AnswerText): ResponseEntity<*> {
        service.updateAnswer(surveyId, answerId, answer)
        return ResponseEntity.noContent().build<Any>()
    }

    @DeleteMapping("/{answerId}")
    @ApiOperation("Delete answer")
    fun deleteAnswer(@PathVariable surveyId: String, @PathVariable answerId: String): ResponseEntity<*> {
        service.deleteAnswer(surveyId, answerId)
        return ResponseEntity.ok().build<Any>()
    }

    @GetMapping("/{questionId}")
    @ApiOperation("Get all answers for question")
    fun getAllAnswers(@PathVariable surveyId: String, @PathVariable questionId: String): List<AnswerIdAndText> {
        return service.getAllAnswers(surveyId, questionId)
    }

    private fun getUri(id: String): URI {
        return ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(id).toUri()
    }
}
