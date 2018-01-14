package com.draganlj.survey.analytics.api;

import com.draganlj.survey.analytics.api.dto.QuestionStatistics;
import com.draganlj.survey.analytics.service.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/analytics/{surveyId}", headers = "Accept=application/vnd.survey-1.0+json")
@Api(description = "Result analytics on single survey level")
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class SurveyResultAnalyticsController {

    private StatisticsService service;

    @GetMapping("/{questionId}")
    @ApiOperation("Get single question statistical distribution")
    public QuestionStatistics getSingleQuestionsStatistic(@PathVariable String surveyId, @PathVariable Integer questionId) {
        return service.getQuestionStatistics(surveyId, questionId);
    }

}
