package com.devotion.capture.service;

import com.devotion.capture.dto.QuestionAnswerDto;
import com.devotion.capture.model.QuestionAnswer;
import com.devotion.capture.model.User;

import java.util.List;

public interface SurveyCaptureService {

    void submitWholeSurvey(User user, List<QuestionAnswerDto> surveyAnswers, String surveyId);

    List<QuestionAnswer> getAnswersOnQuestion(String surveyId, Integer questionId);
}
