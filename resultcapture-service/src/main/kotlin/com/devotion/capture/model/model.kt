package com.devotion.capture.model

import com.devotion.capture.NoArgConstructor
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@NoArgConstructor
open class AnonymousUser(val uniqueId: String)

@Document(collection = "surveyresults")
@NoArgConstructor
class SurveyResult(@Id val id: String? = null, val surveyId: String, val user: AnonymousUser, val submitDate: LocalDateTime, val answers: List<QuestionAnswer>)

@NoArgConstructor
class QuestionAnswer(var surveyResultId: String, var surveyId: String, var questionId: Int, var answerIds: Array<String>)