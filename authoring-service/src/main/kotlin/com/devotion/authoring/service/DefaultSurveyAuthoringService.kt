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

    override fun addQuestion(@NotEmpty surveyId: String, @Valid question: QuestionText) {
        kafkaTemplate.send(GenericMessage(ModifyQuestionEvent(Action.CREATE, surveyId, null, question),
                mapOf<String, Any>(KafkaHeaders.TOPIC to kafkaConfig.questionCapturedTopic)))
    }

    override fun updateQuestion(@NotEmpty surveyId: String, @NotNull questionId: Int, @Valid question: QuestionText) {
        kafkaTemplate.send(GenericMessage(ModifyQuestionEvent(Action.UPDATE, surveyId, questionId, question),
                mapOf<String, Any>(KafkaHeaders.TOPIC to kafkaConfig.questionCapturedTopic)))
    }

    override fun deleteQuestion(@NotEmpty surveyId: String, @NotNull questionId: Int) {
        kafkaTemplate.send(GenericMessage(ModifyQuestionEvent(Action.DELETE, surveyId, questionId),
                mapOf<String, Any>(KafkaHeaders.TOPIC to kafkaConfig.questionCapturedTopic)))
    }

    @KafkaListener(topics = ["\${kafka.questionCapturedTopic}"], containerFactory = "jsonKafkaListenerContainerFactory", errorHandler = "validationErrorHandler")
    fun onModifyQuestionEvent(event: ModifyQuestionEvent) {
        val survey = getValidSurvey(surveyRepository.findById(event.surveyId))
        validateModifyEvent(event, survey)
        val newQuestion = modelMapper.map(event.question, Question::class.java)
        when (event.action) {
            Action.CREATE -> {
                newQuestion.id = survey.questions.size
                survey.questions.add(newQuestion)
            }
            Action.UPDATE -> {
                newQuestion.id = event.questionId
                survey.questions[event.questionId!!] = newQuestion
            }
            Action.DELETE -> {
                survey.questions.removeAt(event.questionId!!);
            }
        }
        surveyRepository.save(survey)
        kafkaTemplate.send(GenericMessage(survey, mapOf<String, Any>(KafkaHeaders.TOPIC to kafkaConfig.surveyStoredTopic)))
    }

    private fun validateModifyEvent(event: ModifyQuestionEvent, survey: Survey) {
        when (event.action) {
            Action.CREATE -> {
                if (event.question.questionText.isNullOrBlank())
                    throw ValidationException("Question text can not be empty.", "Invalid event [$event].")
            }
            Action.UPDATE -> {
                val exception = ValidationException()
                if (event.questionId == null || event.questionId !in (0 until survey.questions.size - 1)) {
                    exception.messages.push("Question id is not valid.")
                }
                if(event.question.questionText.isNullOrBlank()){
                    exception.messages.push("Question text can't be null or empty.")
                }
                if(exception.messages.isNotEmpty()){
                    exception.messages.push("Invalid event [$event]")
                    throw exception
                }
            }
            Action.DELETE -> {
                if (event.questionId == null || event.questionId !in (0 until survey.questions.size - 1)) {
                    throw ValidationException("Question id is not valid.","Invalid event [$event].")
                }
            }
        }
    }


    override fun getQuestion(@NotEmpty surveyId: String, @NotNull questionId: Int, fetchAnswers: Boolean): QuestionAll {
        log.debug("Invoke get question surveyId={}, questionId={}", surveyId, questionId)
        val survey = validate(surveyRepository.findById(surveyId), questionId)
        val questions = survey.questions
        val question = questions[questionId]
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
        val survey = getValidSurvey(surveyRepository.findById(surveyId))
        val questions = survey.questions
        return modelMapper.map(questions, questionDtoListType)
    }

    override fun getAllAnswers(@NotEmpty surveyId: String, @NotNull questionId: Int): List<AnswerIdAndText> {
        validate(surveyRepository.findById(surveyId), questionId)
        val answers = answerRepository.findBySurveyIdAndQuestionId(surveyId, questionId)
        return modelMapper.map(answers, answersDtoListType)
    }

    override fun updateAnswer(@NotEmpty answerId: String, @Valid answer: AnswerText) {
        val existingOne = answerRepository.findById(answerId).get()
        existingOne.answerText = answer.answerText
        answerRepository.save(answerRepository.findById(answerId).get().apply { answerText = answer.answerText })
    }

    override fun deleteAnswer(@NotEmpty answerId: String) = answerRepository.deleteById(answerId)

    override fun addAnswer(@NotEmpty surveyId: String, @NotNull questionId: Int, @Valid answer: AnswerText): Answer {
        validate(surveyRepository.findById(surveyId), questionId)
        val newAnswer = modelMapper.map(answer, Answer::class.java)
        newAnswer.surveyId = surveyId
        newAnswer.questionId = questionId
        return answerRepository.insert(newAnswer)
    }

    // todo: implement better and more flexible/reusable validation by using spring custom validators
    private fun getValidSurvey(input: Optional<Survey>): Survey {
        if (!input.isPresent) {
            throw ValidationException("Survey [$input] could not be found")
        }
        val result = input.get()
        if (result.published!!) {
            throw ValidationException("Survey [$input] is already published")
        }
        return result
    }

    private fun validate(input: Optional<Survey>, questionId: Int): Survey {
        val survey = getValidSurvey(input)
        val questions = survey.questions
        if (questions.size < questionId) {
            throw ValidationException("Survey [$survey] has no question with id [$questionId]")
        }
        return survey
    }
}
