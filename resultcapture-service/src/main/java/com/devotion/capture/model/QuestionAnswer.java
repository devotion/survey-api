package com.devotion.capture.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
public class QuestionAnswer {

    private String surveyResultId;
    private String surveyId;
    private Integer questionId;
    private String[] answerIds;

}
