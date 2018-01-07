package com.devotion.authoring.service

import com.devotion.authoring.dto.*
import com.devotion.authoring.model.Answer

interface SurveyAuthoringService {

    fun addQuestion(surveyId: String, question: QuestionText)
    fun updateQuestion(surveyId: String, questionId: Int, question: QuestionText)
    fun deleteQuestion(surveyId: String, questionId: Int)
    fun getQuestion(surveyId: String, questionId: Int, fetchAnswers: Boolean): QuestionAll
    fun getAllQuestions(surveyId: String): List<QuestionIdAndText>
    fun getAllAnswers(surveyId: String, questionId: Int): List<AnswerIdAndText>
    fun deleteAnswer(answerId: String)
    fun updateAnswer(answerId: String, answer: AnswerText)
    fun addAnswer(surveyId: String, questionId: Int, answer: AnswerText): Answer

}
