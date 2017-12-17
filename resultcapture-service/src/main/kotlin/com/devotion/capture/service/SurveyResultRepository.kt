package com.devotion.capture.service

import com.devotion.capture.model.QuestionAnswer
import com.devotion.capture.model.SurveyResult
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface SurveyResultRepository : MongoRepository<SurveyResult, String> {

    fun findAnswersBySurveyId(surveyId: String): List<QuestionAnswer>

}


