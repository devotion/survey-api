package com.devotion.authoring.dto

import com.devotion.authoring.NoArgConstructor
enum class Action {
    CREATE, UPDATE, DELETE
}
@NoArgConstructor
class ModifyQuestionEvent(val action: Action, val surveyId: String, val questionId : Int?, val question: QuestionText = QuestionText(null))