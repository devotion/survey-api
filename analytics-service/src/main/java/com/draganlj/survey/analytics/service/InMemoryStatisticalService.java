package com.draganlj.survey.analytics.service;

import com.draganlj.survey.analytics.api.dto.Question;
import com.draganlj.survey.analytics.api.dto.QuestionAnswer;
import com.draganlj.survey.analytics.api.dto.QuestionStatistics;
import com.draganlj.survey.analytics.client.AuthoringServiceClient;
import com.draganlj.survey.analytics.client.ResultCaptureServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InMemoryStatisticalService implements StatisticsService {

    private ResultCaptureServiceClient captureServiceClient;

    private AuthoringServiceClient authoringServiceClient;

    @Override
    // fixme: clients not injected, fix this once we move to events
    public QuestionStatistics getQuestionStatistics(String surveyId, Integer questionId) {
        log.debug("Invoke question statistic surveyid={}, questionid={}", surveyId, questionId);
        Question questionWithAnswers = authoringServiceClient.getQuestionWithAnswers(surveyId, questionId);
        List<QuestionAnswer> answersOnQuestion = captureServiceClient.getAnswersOnQuestion(surveyId, questionId);
        Map<String, Long> distributionMap = answersOnQuestion.stream()
                .map(e -> e.getAnswerIds())
                .flatMap(ans -> Arrays.stream(ans))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        QuestionStatistics stat = QuestionStatistics.builder()
                .answerDistribution(distributionMap)
                .numberOfAnswers(distributionMap.values().stream().mapToLong(Long::longValue).sum())
                .question(questionWithAnswers).build();
        return stat;
    }
}
