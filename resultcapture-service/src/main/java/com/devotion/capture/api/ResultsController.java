package com.devotion.capture.api;

import com.devotion.capture.service.SurveyCaptureService;
import com.devotion.capture.service.UserService;
import com.devotion.capture.model.QuestionAnswer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/results/{surveyId}", headers = "Accept=application/vnd.survey-1.0+json")
@Api(description = "Provides operations to get response data")
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class ResultsController {

    @Autowired
    private SurveyCaptureService captureService;

    @Autowired
    private UserService userService;

    @GetMapping("/{questionId}")
    @ApiOperation("Get all responses on single question.")
    public List<QuestionAnswer> getAnswersOnQuestion(@PathVariable String surveyId, @PathVariable Integer questionId) {
        return captureService.getAnswersOnQuestion(surveyId, questionId);

    }

}
