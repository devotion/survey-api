package com.devotion.authoring.service

import com.devotion.authoring.dto.*
import com.devotion.authoring.model.Answer

interface SurveyAuthoringService {

    fun addQuestion(surveyId: String, question: QuestionText)
    fun updateQuestion(surveyId: String, questionId: String, question: QuestionText)
    fun deleteQuestion(surveyId: String, questionId: String)
    fun getQuestion(surveyId: String, questionId: String, fetchAnswers: Boolean): QuestionAll
    fun getAllQuestions(surveyId: String): List<QuestionIdAndText>
    fun getAllAnswers(surveyId: String, questionId: String): List<AnswerIdAndText>
    fun deleteAnswer(answerId: String)
    fun updateAnswer(answerId: String, answer: AnswerText)
    fun addAnswer(surveyId: String, questionId: String, answer: AnswerText): Answer

}
