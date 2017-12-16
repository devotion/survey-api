package com.devotion.capture.service;

import com.devotion.capture.model.QuestionAnswer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionAnswerRepository extends MongoRepository<QuestionAnswer, String> {

    List<QuestionAnswer> findBySurveyIdAndQuestionId(String surveyId, Integer questionId);

}


