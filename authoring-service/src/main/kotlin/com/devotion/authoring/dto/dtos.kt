package com.devotion.authoring.dto

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

class QuestionText(@NotEmpty var questionText: String? = null)

class AnswerText(@NotEmpty var answerText: String)

class QuestionAll(var questionId: String? = null, var questionText: String? = null, var answers: List<AnswerIdAndText>? = null)

class QuestionIdAndText(@NotNull var questionId: String? = null, @NotEmpty var questionText: String? = null)

class AnswerIdAndText(@NotEmpty var answerId: String? = null, @NotEmpty var answerText: String? = null)

data class Payload<T>(val data: T)