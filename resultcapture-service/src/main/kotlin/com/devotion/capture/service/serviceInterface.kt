package com.devotion.capture.service

import com.devotion.capture.dto.QuestionAnswerDto
import com.devotion.capture.model.AnonimousUser
import com.devotion.capture.model.QuestionAnswer
import javax.servlet.http.HttpServletRequest

interface SurveyCaptureService {

    fun submitWholeSurvey(user: AnonimousUser, surveyAnswers: List<QuestionAnswerDto>, surveyId: String)

    fun getAnswersOnQuestion(surveyId: String, questionId: Int?): List<QuestionAnswer>
}

interface UserService {

    fun resolveUser(request: HttpServletRequest): AnonimousUser

}
