package com.devotion.authoring.service

import com.devotion.authoring.KafkaConfig
import com.devotion.authoring.ValidationException
import com.devotion.authoring.dto.*
import com.devotion.authoring.model.Answer
import com.devotion.authoring.model.Question
import com.devotion.authoring.model.Survey
import org.modelmapper.ModelMapper
import org.modelmapper.TypeToken
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.support.GenericMessage
import org.springframework.stereotype.Service
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Service
class DefaultSurveyAuthoringService(private val surveyRepository: SurveyRepository,
                                    private val answerRepository: AnswerRepository,
                                    private val modelMapper: ModelMapper,
                                    private val kafkaConfig: KafkaConfig,
                                    private val kafkaTemplate: KafkaTemplate<String, String>) : SurveyAuthoringService {

    private val log = LoggerFactory.getLogger(DefaultSurveyAuthoringService::class.java)
    private val questionDtoListType = object : TypeToken<List<QuestionIdAndText>>() {}.type
    private val answersDtoListType = object : TypeToken<List<AnswerIdAndText>>() {}.type

    override fun addQuestion(@NotEmpty surveyId: String, @Valid question: QuestionText) {
        kafkaTemplate.sendGenericMessage(ModifyQuestionEvent(Action.CREATE, surveyId, null, question), kafkaConfig.questionCapturedTopic)
    }

    override fun updateQuestion(@NotEmpty surveyId: String, @NotNull questionId: String, @Valid question: QuestionText) {
        kafkaTemplate.sendGenericMessage(ModifyQuestionEvent(Action.UPDATE, surveyId, questionId, question), kafkaConfig.questionCapturedTopic)
    }

    override fun deleteQuestion(@NotEmpty surveyId: String, @NotNull questionId: String) {
        kafkaTemplate.sendGenericMessage(ModifyQuestionEvent(Action.DELETE, surveyId, questionId), kafkaConfig.questionCapturedTopic)
    }

    // todo: make sure to not retry on validationexception!!!
    @KafkaListener(topics = ["\${kafka.questionCapturedTopic}"], containerFactory = "jsonKafkaListenerContainerFactory", errorHandler = "validationErrorHandler")
    fun onModifyQuestionEvent(event: ModifyQuestionEvent) {
        val survey = getValidSurvey(event.surveyId)
        validateModifyEvent(event, survey)
        val newQuestion = modelMapper.map(event.question, Question::class.java)
        if (event.questionId != null)
            newQuestion.id = event.questionId
        when (event.action) {
            Action.CREATE -> survey.questions.add(newQuestion)
            Action.UPDATE -> survey.questions[survey.questions.indexOfFirst { it.id == newQuestion.id }] = newQuestion
            Action.DELETE -> survey.questions.removeAt(survey.questions.indexOfFirst { it.id == newQuestion.id })
        }
        surveyRepository.save(survey)
        kafkaTemplate.sendGenericMessage(survey, kafkaConfig.surveyStoredTopic)
    }

    override fun addAnswer(@NotEmpty surveyId: String, @NotNull questionId: String, @Valid answer: AnswerText): Answer {
        validate(surveyId, questionId)
        return answerRepository.insert(modelMapper.map(answer, Answer::class.java).apply { this.surveyId = surveyId })
    }

    override fun updateAnswer(@NotEmpty answerId: String, @Valid answer: AnswerText) {
        answerRepository.save(answerRepository.findById(answerId).get().apply { answerText = answer.answerText })
    }

    override fun deleteAnswer(@NotEmpty answerId: String) = answerRepository.deleteById(answerId)

    override fun getQuestion(@NotEmpty surveyId: String, @NotNull questionId: String, fetchAnswers: Boolean): QuestionAll {
        log.debug("Invoke get question surveyId={}, questionId={}", surveyId, questionId)
        val survey = validate(surveyId, questionId)
        val questions = survey.questions
        val question = questions.first { it.id == questionId }
        val questionDto = modelMapper.map(question, QuestionAll::class.java)
        if (fetchAnswers) {
            val answers = answerRepository.findBySurveyIdAndQuestionId(surveyId, questionId)
            if (answers.isNotEmpty()) {
                questionDto.answers = modelMapper.map(answers, answersDtoListType)
            }
        }
        return questionDto
    }

    override fun getAllQuestions(@NotEmpty surveyId: String): List<QuestionIdAndText> =
            modelMapper.map(getValidSurvey(surveyId).questions, questionDtoListType)

    override fun getAllAnswers(@NotEmpty surveyId: String, @NotNull questionId: String): List<AnswerIdAndText> {
        validate(surveyId, questionId)
        val answers = answerRepository.findBySurveyIdAndQuestionId(surveyId, questionId)
        return modelMapper.map(answers, answersDtoListType)
    }

    // todo: implement better and more flexible/reusable validation by using spring custom validators
    private fun getValidSurvey(surveyId: String): Survey {
        val input = surveyRepository.findById(surveyId)
        if (!input.isPresent) {
            throw ValidationException("Survey with id [$surveyId] could not be found")
        }
        val result = input.get()
        if (result.published!!) {
            throw ValidationException("Survey [$surveyId] is already published")
        }
        return result
    }

    private fun validate(surveyId: String, questionId: String): Survey {
        val survey = getValidSurvey(surveyId)
        if (survey.questions.filter { it.id == questionId }.size != 1) {
            throw ValidationException("Survey [$survey] has no question with id [$questionId]")
        }
        return survey
    }

    private fun validateModifyEvent(event: ModifyQuestionEvent, survey: Survey) {
        when (event.action) {
            Action.CREATE -> {
                if (event.question.questionText.isNullOrBlank())
                    throw ValidationException("Question text can not be empty.")
            }
            Action.UPDATE -> {
                val exception = ValidationException()
                if (event.questionId == null || survey.questions.filter { it.id == event.questionId }.size != 1) {
                    exception.messages.push("Question id is not valid.")
                }
                if (event.question.questionText.isNullOrBlank()) {
                    exception.messages.push("Question text can't be null or empty.")
                }
                if (exception.messages.isNotEmpty()) {
                    throw exception
                }
            }
            Action.DELETE -> {
                if (event.questionId == null || survey.questions.filter { it.id == event.questionId }.size != 1) {
                    throw ValidationException("Question id is not valid.")
                }
            }
        }
    }

    private fun KafkaTemplate<String, String>.sendGenericMessage(event: Any, topic: String) =
            send(GenericMessage(event, mapOf<String, Any>(KafkaHeaders.TOPIC to topic)))
}
