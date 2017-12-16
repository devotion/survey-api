package com.devotion.capture.dto;

import lombok.Data;
import javax.validation.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class QuestionAnswerDto implements Serializable {

    @NotNull
    private Integer questionId;

    @NotEmpty
    @Valid
    private String[] answerIds;

}
