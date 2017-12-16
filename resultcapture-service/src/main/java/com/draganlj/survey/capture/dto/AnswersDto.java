package com.draganlj.survey.capture.dto;

import lombok.Data;
import javax.validation.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.List;

@Data
public class AnswersDto {

    @NotEmpty
    @Valid
    private List<QuestionAnswerDto> answers;

}
