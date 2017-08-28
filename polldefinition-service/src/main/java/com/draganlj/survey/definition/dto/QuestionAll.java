package com.draganlj.survey.definition.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionAll {

    private Integer questionId;

    private String questionText;

    private List<AnswerIdAndText> answers;

}
