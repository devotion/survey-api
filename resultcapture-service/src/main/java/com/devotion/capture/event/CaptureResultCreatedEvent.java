package com.devotion.capture.event;

import com.devotion.capture.dto.QuestionAnswerDto;
import com.devotion.capture.model.AnonumousUser;
import com.devotion.capture.model.User;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptureResultCreatedEvent implements Serializable {

    @JsonDeserialize(as = AnonumousUser.class)
    private User user;

    private String surveyId;

    private List<QuestionAnswerDto> answers;

}
