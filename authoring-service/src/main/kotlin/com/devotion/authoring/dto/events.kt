package com.devotion.authoring.dto

import com.devotion.authoring.NoArgConstructor
import com.fasterxml.jackson.annotation.JsonTypeInfo

interface Event {

}

@NoArgConstructor
@JsonTypeInfo(use= JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY, property="class")
abstract class QuestionEvent ( val surveyId: String, open val question: QuestionText) : Event

@NoArgConstructor
class AddQuestionEvent(surveyId: String, question: QuestionText) : QuestionEvent( surveyId, question), Event

@NoArgConstructor
class UpdateQuestionEvent(surveyId: String, val questionId: String, override var question: QuestionText) : QuestionEvent( surveyId, question)

@NoArgConstructor
class DeleteQuestionEvent(surveyId: String, val questionId: String): QuestionEvent( surveyId, QuestionText())

@NoArgConstructor
@JsonTypeInfo(use= JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY, property="class")
abstract class AnswerEvent(val surveyId: String, val answer: AnswerText) : Event

@NoArgConstructor
class AddAnswerEvent(surveyId: String, val questionId: String, answerText: AnswerText) : AnswerEvent(surveyId, answerText)

@NoArgConstructor
class UpdateAnswerEvent(surveyId: String, val answerId: String, answerText: AnswerText) : AnswerEvent(surveyId, answerText)

@NoArgConstructor
class DeleteAnswerEvent(surveyId: String, val answerId: String) : AnswerEvent(surveyId, AnswerText(""))

@NoArgConstructor
class ProcessingEventError(val error: String?, val originalEvent: Any, val __messageHeaders: Map<String, Any>)
