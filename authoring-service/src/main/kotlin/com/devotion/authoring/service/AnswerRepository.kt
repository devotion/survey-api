package com.devotion.authoring.service

import com.devotion.authoring.model.Answer
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface AnswerRepository : MongoRepository<Answer, String> {
    fun findBySurveyIdAndQuestionId(surveyId: String, questionId: Int?): List<Answer>
}


