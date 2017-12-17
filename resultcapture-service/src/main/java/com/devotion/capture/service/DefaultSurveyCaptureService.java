package com.devotion.capture.service;

import com.devotion.capture.dto.QuestionAnswerDto;
import com.devotion.capture.event.CaptureResultCreatedEvent;
import com.devotion.capture.model.QuestionAnswer;
import com.devotion.capture.model.SurveyResult;
import com.devotion.capture.model.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class DefaultSurveyCaptureService implements SurveyCaptureService {

    @Value("${kafka.result-captured-topic}")
    private String resultCaptureTopic;

    @Value("${kafka.result-stored-topic}")
    private String resultStoreTopic;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SurveyResultRepository resultRepository;

    @Autowired
    private QuestionAnswerRepository answerRepository;

    @Autowired
    private KafkaTemplate<String, ?> kafkaTemplate;

    private final Type questionAnswerModelType = new TypeToken<List<QuestionAnswer>>() {
    }.getType();

    @Override
    public void submitWholeSurvey(User user, @NotEmpty List<QuestionAnswerDto> surveyAnswers, @NotEmpty String surveyId) {
        sendTo(resultCaptureTopic, new CaptureResultCreatedEvent(user, surveyId, surveyAnswers));
    }

    @KafkaListener(topics = "result-captured", containerFactory = "jsonKafkaListenerContainerFactory")
    public void storeResult(CaptureResultCreatedEvent captureEvent) {

        // 1. convert from dto
        List<QuestionAnswer> answers = modelMapper.map(captureEvent.getAnswers(), questionAnswerModelType);
        SurveyResult newResult = SurveyResult.builder()
                .surveyId(captureEvent.getSurveyId())
                // todo: make sure you are able to calculate user time!
                .submitDate(LocalDateTime.now())
                .user(captureEvent.getUser())
                .answers(answers)
                .build();
        // 2. validate
        validate(newResult);
        // 3. store
        SurveyResult surveyResult = resultRepository.insert(newResult);
        // 4. send success persistence event
        sendTo(resultStoreTopic, surveyResult);
    }

    private void sendTo(String topic, Object message) {
        kafkaTemplate.send(new GenericMessage<>(message, Collections.singletonMap(KafkaHeaders.TOPIC, topic)));
    }

    private void validate(SurveyResult newResult) {

    }

    @Override
    public List<QuestionAnswer> getAnswersOnQuestion(@NotEmpty String surveyId, @NotNull Integer questionId) {
        log.debug("Invoke getAnswersOnQuestion statistic surveyid={}, questionid={}", surveyId, questionId);
        return answerRepository.findBySurveyIdAndQuestionId(surveyId, questionId);
    }
}
