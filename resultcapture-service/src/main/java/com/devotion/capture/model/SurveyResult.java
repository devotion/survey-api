package com.devotion.capture.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Document(collection = "surveyresults")
public class SurveyResult {

    @Id
    private String id;
    private String surveyId;
    private User user;
    private LocalDateTime submitDate;
    private List<QuestionAnswer> answers;

}
