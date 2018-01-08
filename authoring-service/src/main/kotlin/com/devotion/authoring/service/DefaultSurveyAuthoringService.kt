package com.devotion.authoring.service

import com.devotion.authoring.ValidationException
import com.devotion.authoring.dto.*
import com.devotion.authoring.model.Answer
import com.devotion.authoring.model.Question
import com.devotion.authoring.model.Survey
import org.modelmapper.ModelMapper
import org.modelmapper.TypeToken
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.support.GenericMessage
import org.springframework.stereotype.Service
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import com.devotion.authoring.KafkaConfig
import org.springframework.kafka.annotation.KafkaListener


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
                Collections.singletonMap<String, Any>(KafkaHeaders.TOPIC, kafkaConfig.questionCapturedTopic)))
    }

    override fun updateQuestion(@NotEmpty surveyId: String, @NotNull questionId: Int, @Valid question: QuestionText) {
        kafkaTemplate.send(GenericMessage(ModifyQuestionEvent(Action.UPDATE, surveyId, questionId, question),
                Collections.singletonMap<String, Any>(KafkaHeaders.TOPIC, kafkaConfig.questionCapturedTopic)))
    }

    override fun deleteQuestion(@NotEmpty surveyId: String, @NotNull questionId: Int) {
        throw RuntimeException("not implemented")
    }

    @KafkaListener(topics = ["\${kafka.questionCapturedTopic}"], containerFactory = "jsonKafkaListenerContainerFactory")
    fun onModifyQuestionEvent(event: ModifyQuestionEvent) {
        val survey = validate(surveyRepository.findById(event.surveyId))
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
        kafkaTemplate.send(GenericMessage(survey, Collections.singletonMap<String, Any>(KafkaHeaders.TOPIC, kafkaConfig.surveyStoredTopic)))
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
        val survey = validate(surveyRepository.findById(surveyId))
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
    private fun validate(input: Optional<Survey>): Survey {
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
        val survey = validate(input)
        val questions = survey.questions
        if (questions.size < questionId) {
            throw ValidationException("Survey [$survey] has no question with id [$questionId]")
        }
        return survey
    }
}
