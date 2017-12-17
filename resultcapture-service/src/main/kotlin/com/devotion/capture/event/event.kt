package com.devotion.capture.event

import com.devotion.capture.NoArgConstructor
import com.devotion.capture.dto.QuestionAnswerDto
import com.devotion.capture.model.AnonymousUser
import java.io.Serializable

@NoArgConstructor
class CaptureResultCreatedEvent(
        val user: AnonymousUser,
        val surveyId: String,
        val answers: List<QuestionAnswerDto>) : Serializable
