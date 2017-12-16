package com.draganlj.survey.capture.event;

import com.draganlj.survey.capture.model.QuestionAnswer;
import com.draganlj.survey.capture.model.SurveyResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptureResultCreatedEvent {

    private SurveyResult surveyResult;

    private List<QuestionAnswer> answers;

    private boolean persisted;

}
