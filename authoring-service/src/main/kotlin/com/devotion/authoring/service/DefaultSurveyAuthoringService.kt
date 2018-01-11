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
import org.springframework.beans.factory.annotation.Autowired
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
class DefaultSurveyAuthoringService(@Autowired private val surveyRepository: SurveyRepository,
                                    @Autowired private val answerRepository: AnswerRepository,
                                    @Autowired private val modelMapper: ModelMapper,
                                    @Autowired private val kafkaConfig: KafkaConfig,
                                    @Autowired private val kafkaTemplate: KafkaTemplate<String, String>) : SurveyAuthoringService {

    private val log = LoggerFactory.getLogger(DefaultSurveyAuthoringService::class.java)
    private val questionDtoListType = object : TypeToken<List<QuestionIdAndText>>() {}.type
    private val answersDtoListType = object : TypeToken<List<AnswerIdAndText>>() {}.type

    /**
     * Write functions
     */
    override fun addQuestion(@NotEmpty surveyId: String, @Valid question: QuestionText) {
        kafkaTemplate.send(createGenericMessage(ModifyQuestionEvent(Action.CREATE, surveyId, null, question), kafkaConfig.questionCapturedTopic))
    }

    override fun updateQuestion(@NotEmpty surveyId: String, @NotNull questionId: String, @Valid question: QuestionText) {
        kafkaTemplate.send(createGenericMessage(ModifyQuestionEvent(Action.UPDATE, surveyId, questionId, question), kafkaConfig.questionCapturedTopic))
    }

    override fun deleteQuestion(@NotEmpty surveyId: String, @NotNull questionId: String) {
        kafkaTemplate.send(createGenericMessage(ModifyQuestionEvent(Action.DELETE, surveyId, questionId), kafkaConfig.questionCapturedTopic))
    }

    @KafkaListener(topics = ["\${kafka.questionCapturedTopic}"], containerFactory = "jsonKafkaListenerContainerFactory", errorHandler = "validationErrorHandler")
    fun onModifyQuestionEvent(event: ModifyQuestionEvent) {
        val survey = getValidSurvey(event.surveyId)
        validateModifyEvent(event, survey)
        val newQuestion = modelMapper.map(event.question, Question::class.java)
        if (event.questionId != null)
            newQuestion.id = event.questionId
        when (event.action) {
            Action.CREATE -> {
                survey.questions.add(newQuestion)
            }
            Action.UPDATE -> {
                survey.questions.set(survey.questions.indexOfFirst { it.id == newQuestion.id }, newQuestion)
            }
            Action.DELETE -> {
                survey.questions.removeAt(survey.questions.indexOfFirst { it.id == newQuestion.id });
            }
        }
        surveyRepository.save(survey)
        kafkaTemplate.send(GenericMessage(survey, Collections.singletonMap<String, Any>(KafkaHeaders.TOPIC, kafkaConfig.surveyStoredTopic)))
    }

    override fun addAnswer(@NotEmpty surveyId: String, @NotNull questionId: String, @Valid answer: AnswerText): Answer {
        validate(surveyId, questionId)
        val newAnswer = modelMapper.map(answer, Answer::class.java)
        newAnswer.surveyId = surveyId
        return answerRepository.insert(newAnswer)
    }

    override fun updateAnswer(@NotEmpty answerId: String, @Valid answer: AnswerText) {
        val existingOne = answerRepository.findById(answerId).get()
        existingOne.answerText = answer.answerText
        answerRepository.save(answerRepository.findById(answerId).get().apply { answerText = answer.answerText })
    }

    override fun deleteAnswer(@NotEmpty answerId: String) = answerRepository.deleteById(answerId)


    /**
     * Read functions
     */
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

    override fun getAllQuestions(@NotEmpty surveyId: String): List<QuestionIdAndText> {
        val survey = getValidSurvey(surveyId)
        val questions = survey.questions
        return modelMapper.map(questions, questionDtoListType)
    }

    override fun getAllAnswers(@NotEmpty surveyId: String, @NotNull questionId: String): List<AnswerIdAndText> {
        validate(surveyId, questionId)
        val answers = answerRepository.findBySurveyIdAndQuestionId(surveyId, questionId)
        return modelMapper.map(answers, answersDtoListType)
    }


    // todo: implement better and more flexible/reusable validation by using spring custom validators
    private fun getValidSurvey(surveyId: String): Survey {
        var input = surveyRepository.findById(surveyId)
        if (!input.isPresent) {
            throw ValidationException("Survey [$input] could not be found")
        }
        val result = input.get()
        if (result.published!!) {
            throw ValidationException("Survey [$input] is already published")
        }
        return result
    }

    private fun validate(surveyId: String, questionId: String): Survey {
        val survey = getValidSurvey(surveyId)
        val questions = survey.questions
        if (survey.questions.filter { it.id == questionId }.size != 1) {
            throw ValidationException("Survey [$survey] has no question with id [$questionId]")
        }
        return survey
    }

    private fun validateModifyEvent(event: ModifyQuestionEvent, survey: Survey) {
        when (event.action) {
            Action.CREATE -> {
                if (event.question.questionText.isNullOrBlank())
                    throw ValidationException("Question text can not be empty.", "Invalid event [$event].")
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
                    exception.messages.push("Invalid event [$event]")
                    throw exception
                }
            }
            Action.DELETE -> {
                if (event.questionId == null || survey.questions.filter { it.id == event.questionId }.size != 1) {
                    throw ValidationException("Question id is not valid.", "Invalid event [$event].")
                }
            }
        }
    }

    private fun createGenericMessage(event: Any, topic: String) =
            GenericMessage(event, Collections.singletonMap<String, Any>(KafkaHeaders.TOPIC, topic))

}
