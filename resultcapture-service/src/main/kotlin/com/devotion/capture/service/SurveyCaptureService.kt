package com.devotion.capture.service

import com.devotion.capture.dto.QuestionAnswerDto
import com.devotion.capture.model.AnonimousUser
import com.devotion.capture.model.QuestionAnswer

interface SurveyCaptureService {

    fun submitWholeSurvey(user: AnonimousUser, surveyAnswers: List<QuestionAnswerDto>, surveyId: String)

    fun getAnswersOnQuestion(surveyId: String, questionId: Int?): List<QuestionAnswer>
}
