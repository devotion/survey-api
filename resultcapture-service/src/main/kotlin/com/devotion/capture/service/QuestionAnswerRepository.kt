package com.devotion.capture.service

import com.devotion.capture.model.QuestionAnswer
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface QuestionAnswerRepository : MongoRepository<QuestionAnswer, String> {

    fun findBySurveyIdAndQuestionId(surveyId: String, questionId: Int?): List<QuestionAnswer>

}


