package com.devotion.authoring.api

import com.devotion.authoring.service.SurveyAuthoringService
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@RunWith(SpringRunner::class)
@WebMvcTest(QuestionController::class)
@TestPropertySource(locations = arrayOf("classpath:application.yml"))
class QuestionControllerTestKotlin {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var service: SurveyAuthoringService

    @Before
    fun setup() {
        //Mockito.`when`(service.addQuestion(Matchers.eq("1"), Matchers.any<QuestionText>())).thenReturn(Question(2))
    }

    @Test
    fun `returns HTTP bad request on wrong input`() {
        //Mockito.doThrow(ValidationException::class.java).`when`(service).addQuestion(Matchers.anyString(), Matchers.any())
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/questions/3/")
                .content("{ \"questionText\": \"Fourth question\"}")
                .contentType("application/vnd.survey-1.0+json")
                .header("Accept", "application/vnd.survey-1.0+json"))
        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest)
    }
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