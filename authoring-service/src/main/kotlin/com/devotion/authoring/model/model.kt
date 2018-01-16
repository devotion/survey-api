package com.devotion.authoring.model

import com.devotion.authoring.NoArgConstructor
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "answers")
class Answer {
    @Id
    lateinit var id: String
    lateinit var answerText: String
    lateinit var surveyId: String
    lateinit var questionId: String
}

@NoArgConstructor
class Question( var questionText: String){
    var id: String = ""
}

@Document(collection = "surveys")
class Survey(
        var id: String? = null,
        var author: String? = null,
        var createDate: Date? = null,
        var surveyTitle: String? = null,
        var published: Boolean? = null,
        var questions: MutableList<Question> = mutableListOf()
)

