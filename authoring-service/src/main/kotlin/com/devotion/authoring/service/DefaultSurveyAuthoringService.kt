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
import java.util.*
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
        kafkaTemplate.sendGenericMessage(AddQuestionEvent(surveyId, question), kafkaConfig.questionCapturedTopic)
    }

    override fun updateQuestion(@NotEmpty surveyId: String, @NotNull questionId: String, @Valid question: QuestionText) {
        kafkaTemplate.sendGenericMessage(UpdateQuestionEvent(surveyId, questionId, question), kafkaConfig.questionCapturedTopic)
    }

    override fun deleteQuestion(@NotEmpty surveyId: String, @NotNull questionId: String) {
        kafkaTemplate.sendGenericMessage(DeleteQuestionEvent(surveyId, questionId), kafkaConfig.questionCapturedTopic)
    }


    // todo: make sure to not retry on validationexception!!!
    @KafkaListener(topics = ["\${kafka.questionCapturedTopic}"], containerFactory = "jsonKafkaListenerContainerFactory", errorHandler = "validationErrorHandler")
    fun onModifyQuestionEvent(event: QuestionEvent) {
        val survey = getValidSurvey(event.surveyId)
        validateModifyEvent(event, survey)
        when (event) {
            is AddQuestionEvent ->
                survey.questions.add(modelMapper.map(event.question, Question::class.java).apply { this.id = UUID.randomUUID().toString() })
            is UpdateQuestionEvent ->
                survey.questions[survey.questions.indexOfFirst { it.id == event.questionId }] = modelMapper.map(event.question, Question::class.java).apply { this.id = event.questionId }
            is DeleteQuestionEvent -> survey.questions.removeAt(survey.questions.indexOfFirst { it.id == event.questionId })
        }
        surveyRepository.save(survey)
        kafkaTemplate.sendGenericMessage(survey, kafkaConfig.surveyStoredTopic)
    }

    override fun addAnswer(@NotEmpty surveyId: String, @NotNull questionId: String, @Valid answer: AnswerText) {
        kafkaTemplate.sendGenericMessage(AddAnswerEvent(surveyId, questionId, answer), kafkaConfig.answerCapturedTopic)
    }

    override fun updateAnswer(@NotEmpty surveyId: String, @NotEmpty answerId: String, @Valid answer: AnswerText) {
        kafkaTemplate.sendGenericMessage(UpdateAnswerEvent(surveyId, answerId, answer), kafkaConfig.answerCapturedTopic)
    }

    override fun deleteAnswer(@NotEmpty surveyId: String, @NotEmpty answerId: String) {
        kafkaTemplate.sendGenericMessage(DeleteAnswerEvent(surveyId, answerId), kafkaConfig.answerCapturedTopic)
    }

    @KafkaListener(topics = ["\${kafka.answerCapturedTopic}"], containerFactory = "jsonKafkaListenerContainerFactory", errorHandler = "validationErrorHandler")
    fun onModifyAnswerEvent(event: AnswerEvent) {
        // todo - validation
        when (event) {
            is AddAnswerEvent ->
                answerRepository.insert(modelMapper.map(event.answer, Answer::class.java).apply {
                    this.surveyId = event.surveyId
                    this.questionId = event.questionId
                })

            is UpdateAnswerEvent ->
                answerRepository.save(answerRepository.findById(event.answerId).get().apply { answerText = event.answer.answerText })
            is DeleteAnswerEvent ->
                answerRepository.deleteById(event.answerId)

        }
    }

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

    override fun getAllQuestions(@NotEmpty surveyId: String): MutableList<QuestionIdAndText> =
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

    private fun validateModifyEvent(event: QuestionEvent, survey: Survey) {
        when (event) {
            is AddQuestionEvent -> {
                if (event.question.questionText.isNullOrBlank())
                    throw ValidationException("Question text can not be empty.")
            }
            is UpdateQuestionEvent -> {
                val exception = ValidationException()
                if (survey.questions.filter { it.id == event.questionId }.size != 1) {
                    exception.messages.push("Question id is not valid.")
                }
                if (event.question.questionText.isNullOrBlank()) {
                    exception.messages.push("Question text can't be null or empty.")
                }
                if (exception.messages.isNotEmpty()) {
                    throw exception
                }
            }
            is DeleteQuestionEvent -> {
                if (survey.questions.filter { it.id == event.questionId }.size != 1) {
                    throw ValidationException("Question id is not valid.")
                }
            }
        }
    }

    private fun KafkaTemplate<String, String>.sendGenericMessage(event: Any, topic: String) =
            send(GenericMessage(event, mapOf<String, Any>(KafkaHeaders.TOPIC to topic)))
}
