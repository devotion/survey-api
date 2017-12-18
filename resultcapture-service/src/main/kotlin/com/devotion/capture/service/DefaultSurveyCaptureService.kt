package com.devotion.capture.service

import com.devotion.capture.KafkaConfig
import com.devotion.capture.dto.QuestionAnswerDto
import com.devotion.capture.event.CaptureResultCreatedEvent
import com.devotion.capture.model.AnonymousUser
import com.devotion.capture.model.QuestionAnswer
import com.devotion.capture.model.SurveyResult
import org.modelmapper.ModelMapper
import org.modelmapper.TypeToken
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.support.GenericMessage
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Service
class DefaultSurveyCaptureService(@Autowired private val kafkaConfig: KafkaConfig,
                                  @Autowired private val modelMapper: ModelMapper,
                                  @Autowired private val resultRepository: SurveyResultRepository,
                                  @Autowired private val answerRepository: QuestionAnswerRepository,
                                  @Autowired private val kafkaTemplate: KafkaTemplate<String, String>) : SurveyCaptureService {

    private val log = LoggerFactory.getLogger(DefaultSurveyCaptureService::class.java)
    private val questionAnswerModelType = object : TypeToken<List<QuestionAnswer>>() {}.type

    override fun submitWholeSurvey(user: AnonymousUser, @NotEmpty surveyAnswers: List<QuestionAnswerDto>, @NotEmpty surveyId: String) =
            sendTo(kafkaConfig.resultCapturedTopic, CaptureResultCreatedEvent(user, surveyId, surveyAnswers))

    @KafkaListener(topics = ["result-captured"], containerFactory = "jsonKafkaListenerContainerFactory")
    fun storeResult(captureEvent: CaptureResultCreatedEvent) {
        val answers = modelMapper.map<List<QuestionAnswer>>(captureEvent.answers, questionAnswerModelType)
        val newResult = SurveyResult(surveyId = captureEvent.surveyId, submitDate = LocalDateTime.now(), user = captureEvent.user, answers = answers)
        validate(newResult)
        val surveyResult = resultRepository.insert(newResult)
        sendTo(kafkaConfig.resultStoredTopic, surveyResult)
    }

    private fun sendTo(topic: String, message: Any) {
        kafkaTemplate.send(GenericMessage(message, Collections.singletonMap<String, Any>(KafkaHeaders.TOPIC, topic)))
    }

    private fun validate(newResult: SurveyResult) {
    }

    override fun getAnswersOnQuestion(@NotEmpty surveyId: String, @NotNull questionId: Int?): List<QuestionAnswer> {
        log.debug("Invoke getAnswersOnQuestion statistic surveyid={}, questionid={}", surveyId, questionId)
        return answerRepository.findBySurveyIdAndQuestionId(surveyId, questionId)
    }
}