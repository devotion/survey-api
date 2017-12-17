package com.devotion.authoring.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "answers")
class Answer(@Id val id: String, var answerText: String, var surveyId: String, var questionId: Int)

@Document
class Question(var id: Int? = null, var questionText: String? = null)

@Document(collection = "surveys")
class Survey(
        var id: String? = null,
        var author: String? = null,
        var createDate: Date? = null,
        var surveyTitle: String? = null,
        var published: Boolean? = null,
        var questions: MutableList<Question> = mutableListOf()
)

