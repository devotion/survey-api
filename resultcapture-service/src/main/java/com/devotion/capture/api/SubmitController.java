package com.devotion.capture.api;

import com.devotion.capture.service.SurveyCaptureService;
import com.devotion.capture.service.UserService;
import com.devotion.capture.dto.AnswersDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/capture/{surveyId}", headers = "Accept=application/vnd.survey-1.0+json")
@Api(description = "Operations needed to capture submission of survey")
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class SubmitController {

    @Autowired
    private SurveyCaptureService captureService;

    @Autowired
    private UserService userService;

    @PostMapping("/")
    @ApiOperation(value = "Submit survey with all answers.", code = HttpServletResponse.SC_CREATED)
    public ResponseEntity<?> submitSurvey(@PathVariable String surveyId, @RequestBody @Valid AnswersDto surveyAnswers, HttpServletRequest request) {
        captureService.submitWholeSurvey(userService.resolveUser(request), surveyAnswers.getAnswers(), surveyId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
