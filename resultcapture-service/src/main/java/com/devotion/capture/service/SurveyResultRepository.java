package com.devotion.capture.service;

import com.devotion.capture.model.SurveyResult;
import com.devotion.capture.model.QuestionAnswer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyResultRepository extends MongoRepository<SurveyResult, String> {

    List<QuestionAnswer> findAnswersBySurveyId(String surveyId);

}


