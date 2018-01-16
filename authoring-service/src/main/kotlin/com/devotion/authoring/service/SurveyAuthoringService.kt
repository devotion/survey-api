package com.devotion.authoring.service

import com.devotion.authoring.dto.*
import com.devotion.authoring.model.Answer
import javax.validation.Valid
import javax.validation.constraints.NotEmpty

interface SurveyAuthoringService {

    fun addQuestion(@NotEmpty surveyId: String, question: QuestionText)
    fun updateQuestion(surveyId: String, questionId: String, question: QuestionText)
    fun deleteQuestion(surveyId: String, questionId: String)

    fun addAnswer(@NotEmpty surveyId: String, questionId: String, answer: AnswerText)
    fun deleteAnswer(@NotEmpty surveyId: String, @NotEmpty answerId: String)
    fun updateAnswer(@NotEmpty surveyId: String, @NotEmpty answerId: String, @Valid answer: AnswerText)

    fun getQuestion(surveyId: String, questionId: String, fetchAnswers: Boolean): QuestionAll
    fun getAllQuestions(surveyId: String): List<QuestionIdAndText>
    fun getAllAnswers(surveyId: String, questionId: String): List<AnswerIdAndText>

}
