package com.devotion.capture.dto

import com.devotion.capture.NoArgConstructor
import java.io.Serializable

@NoArgConstructor
class QuestionAnswerDto (val questionId: Int, val answerIds: Array<String>) : Serializable

@NoArgConstructor
class AnswersDto (var answers: List<QuestionAnswerDto> )
