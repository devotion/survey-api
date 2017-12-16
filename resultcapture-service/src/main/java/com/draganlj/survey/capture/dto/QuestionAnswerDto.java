package com.draganlj.survey.capture.dto;

import lombok.Data;
import javax.validation.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class QuestionAnswerDto {

    @NotNull
    private Integer questionId;

    @NotEmpty
    @Valid
    private String[] answerIds;

}
