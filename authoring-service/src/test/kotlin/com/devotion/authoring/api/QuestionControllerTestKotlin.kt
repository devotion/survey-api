package com.devotion.authoring.api

import com.devotion.authoring.ValidationException
import com.devotion.authoring.dto.QuestionText
import com.devotion.authoring.model.Question
import com.devotion.authoring.service.SurveyAuthoringService
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(SpringRunner::class)
@WebMvcTest(QuestionController::class)
class QuestionControllerTestKotlin {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var service: SurveyAuthoringService

//    @Before
//    fun setup() {
//        Mockito.`when`(service.addQuestion(Matchers.eq("1"), Matchers.any<QuestionText>())).thenReturn(Question(2))
//    }
//
//    @Test
//    fun `create question returns HTTP CREATED with location header`() {
//        val resultActions = mockMvc.perform(post("/questions/1/")
//                .content("{ \"questionText\": \"Fourth question\"}")
//                .contentType("application/vnd.survey-1.0+json")
//                .header("Accept", "application/vnd.survey-1.0+json"))
//        resultActions.andExpect(status().isCreated)
//        resultActions.andExpect(header().string("location", "http://localhost/questions/1/2"))
//    }
//
//    @Test
//    fun `returns HTTP bad request on wrong input`() {
//        Mockito.doThrow(ValidationException::class.java).`when`(service).addQuestion(Matchers.anyString(), Matchers.any())
//        val resultActions = mockMvc.perform(post("/questions/3/")
//                .content("{ \"questionText\": \"Fourth question\"}")
//                .contentType("application/vnd.survey-1.0+json")
//                .header("Accept", "application/vnd.survey-1.0+json"))
//        resultActions.andExpect(status().isBadRequest)
//    }
//
//    @Test
//    fun `returns HTTP 406 when calling add question with wrong header`() {
//        val resultActions = mockMvc.perform(post("/questions/3/")
//                .content("{ \"questionText\": \"Fourth question\"}")
//                .contentType("application/vnd.survey-1.0+json")
//                .header("Accept", "application/vnd.survey-1111.0+json"))
//        resultActions.andExpect(status().isNotAcceptable)
//    }
}