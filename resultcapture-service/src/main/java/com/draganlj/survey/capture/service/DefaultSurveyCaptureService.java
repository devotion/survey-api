package com.draganlj.survey.capture.service;

import com.draganlj.survey.capture.dto.QuestionAnswerDto;
import com.draganlj.survey.capture.event.CaptureResultCreatedEvent;
import com.draganlj.survey.capture.model.QuestionAnswer;
import com.draganlj.survey.capture.model.SurveyResult;
import com.draganlj.survey.capture.model.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class DefaultSurveyCaptureService implements SurveyCaptureService {

    public static final String RESULT_TOPIC = "resultTopic";

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SurveyResultRepository resultRepository;

    @Autowired
    private QuestionAnswerRepository answerRepository;

    @Autowired
    private KafkaTemplate<String, CaptureResultCreatedEvent> kafkaTemplate;

    private final Type questionAnsweristType = new TypeToken<List<QuestionAnswerDto>>() {
    }.getType();
    private final Type questionAnsweristModelType = new TypeToken<List<QuestionAnswer>>() {
    }.getType();

    @Override
    public void submitWholeSurvey(User user, @NotEmpty List<QuestionAnswerDto> surveyAnswers, @NotEmpty String surveyId) {
        // 1. validate
        // 2. convert from dto
        SurveyResult newResult = SurveyResult.builder()
                .surveyId(surveyId)
                // todo: make sure you are able to calculate user time!
                .submitDate(LocalDateTime.now())
                .user(user).build();
        List<QuestionAnswer> answers = modelMapper.map(surveyAnswers, questionAnsweristModelType);

        // 3. send event
        kafkaTemplate.send(RESULT_TOPIC, new CaptureResultCreatedEvent(newResult, answers, false));
    }

    @KafkaListener(topics = RESULT_TOPIC, containerFactory = "filterNonPersistedFactory")
    public void storeResult(CaptureResultCreatedEvent captureEvent){
        SurveyResult insert = resultRepository.insert(captureEvent.getSurveyResult());
        captureEvent.getAnswers().forEach(p -> {
            p.setSurveyResultId(insert.getId());
            p.setSurveyId(insert.getSurveyId());
        });
        answerRepository.insert(captureEvent.getAnswers());
        kafkaTemplate.send(RESULT_TOPIC, new CaptureResultCreatedEvent(captureEvent.getSurveyResult(), captureEvent.getAnswers(), true));
    }


    @Override
    public List<QuestionAnswer> getAnswersOnQuestion(@NotEmpty String surveyId, @NotNull Integer questionId) {
        log.debug("Invoke getAnswersOnQuestion statistic surveyid={}, questionid={}", surveyId, questionId);
        return answerRepository.findBySurveyIdAndQuestionId(surveyId, questionId);
    }
}
