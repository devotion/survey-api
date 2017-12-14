package com.draganlj.survey.authoring.service;


import com.draganlj.survey.authoring.ValidationException;
import com.draganlj.survey.authoring.dto.QuestionAll;
import com.draganlj.survey.authoring.dto.QuestionIdAndText;
import com.draganlj.survey.authoring.dto.QuestionText;
import com.draganlj.survey.authoring.model.Answer;
import com.draganlj.survey.authoring.model.Question;
import com.draganlj.survey.authoring.model.Survey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DefaultSurveyAuthoringServiceTest {

    public static final String SURVEY_ID_0_PUBLISHED = "0";
    public static final String SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS = "1";
    public static final String SURVEY_ID_2_NON_EXISTING = "2";
    public static final String SURVEY_ID_3_NON_PUBLISHED_WITH_1_QUESTION = "3";
    private static final String SURVEY_ID_4_NON_PUBLISHED_WITH_NO_QUESTIONS = "4";

    @Mock
    private SurveyRepository surveyRepository;
    @Mock
    private AnswerRepository answerRepository;

    private final ModelMapper modelMapper = new ModelMapper();

    private DefaultSurveyAuthoringService service;

    @Before
    public void setup() {
        initMocks(this);
        service = new DefaultSurveyAuthoringService(surveyRepository, answerRepository, modelMapper);
        when(surveyRepository.findById(SURVEY_ID_0_PUBLISHED)).thenReturn(Optional.of(Survey.builder().published(Boolean.TRUE).build()));
        when(surveyRepository.findById(SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS)).thenReturn(Optional.of(Survey.builder().published(Boolean.FALSE).questions(getQuestions(4)).build()));
        when(surveyRepository.findById(SURVEY_ID_2_NON_EXISTING)).thenReturn(Optional.empty());
        when(surveyRepository.findById(SURVEY_ID_3_NON_PUBLISHED_WITH_1_QUESTION)).thenReturn(Optional.of(Survey.builder().published(Boolean.FALSE).questions(getQuestions(1)).build()));
        when(surveyRepository.findById(SURVEY_ID_4_NON_PUBLISHED_WITH_NO_QUESTIONS)).thenReturn(Optional.of(Survey.builder().published(Boolean.FALSE).questions(null).build()));
        when(answerRepository.findBySurveyIdAndQuestionId(SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS, 0)).thenReturn(getAnswers(3, 0, SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS));
    }

    @Test
    public void shouldAddQuestion() {
        Question newQuestion = service.addQuestion(SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS, QuestionText.builder().questionText("This is new question").build());
        Assert.assertEquals(Integer.valueOf(4), newQuestion.getId());
    }

    @Test(expected = ValidationException.class)
    public void failAddQuestionForNonExistingSurvey() {
        service.addQuestion(SURVEY_ID_2_NON_EXISTING, QuestionText.builder().questionText("This is new question").build());
    }

    @Test(expected = ValidationException.class)
    public void failAddQuestionForPublishedSurvey() {
        service.addQuestion(SURVEY_ID_0_PUBLISHED, QuestionText.builder().questionText("This is new question").build());
    }

    @Test
    public void shouldUpdateQuestion() {
        Question newQuestion = service.addQuestion("1", QuestionText.builder().questionText("This is new question").build());
        QuestionText map = modelMapper.map(newQuestion, QuestionText.class);
        map.setQuestionText("Updated question text");
        service.updateQuestion(SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS, 4, map);
        Assert.assertEquals("Updated question text", map.getQuestionText());
    }

    @Test(expected = ValidationException.class)
    public void failUpdateQuestionForNonExistingSurvey() {
        service.updateQuestion(SURVEY_ID_2_NON_EXISTING, 1, QuestionText.builder().questionText("This is new question").build());
    }

    @Test(expected = ValidationException.class)
    public void failUpdateQuestionForPublishedSurvey() {
        service.updateQuestion(SURVEY_ID_0_PUBLISHED, 1, QuestionText.builder().questionText("This is new question").build());
    }

    @Test(expected = ValidationException.class)
    public void failUpdateNonExistingQuestion() {
        service.updateQuestion(SURVEY_ID_3_NON_PUBLISHED_WITH_1_QUESTION, 4, QuestionText.builder().questionText("This is new question").build());
    }

    //    @Test
    public void shouldDeleteQuestion() {
        List<Question> questions = getQuestions(3);
        Survey survey = Survey.builder().id("5").questions(questions).published(Boolean.FALSE).build();
        when(surveyRepository.findById("5")).thenReturn(Optional.of(survey));
        service.deleteQuestion("5", 2);
        Assert.assertEquals(2, questions.size());
    }

    //    @Test(expected = ValidationException.class)
    public void failDeleteQuestionForPublishedSurvey() {
        service.deleteQuestion(SURVEY_ID_0_PUBLISHED, 1);
    }

    //    @Test(expected = ValidationException.class)
    public void failDeleteNonExistingQuestion() {
        service.deleteQuestion(SURVEY_ID_3_NON_PUBLISHED_WITH_1_QUESTION, 4);
    }

    @Test
    public void shouldGetAllQuestions() {
        List<QuestionIdAndText> allQuestions = service.getAllQuestions(SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS);
        Assert.assertEquals(4, allQuestions.size());
    }

    @Test
    public void shouldReturnNullWhenNoQuestionsInSurvey() {
        Assert.assertNull(service.getAllQuestions(SURVEY_ID_4_NON_PUBLISHED_WITH_NO_QUESTIONS));
    }

    @Test(expected = ValidationException.class)
    public void failGetAllQuestionsForNonExistingSurvey() {
        service.getAllQuestions(SURVEY_ID_2_NON_EXISTING);
    }

    @Test
    public void shouldGetQuestionWithAnwers() {
        QuestionAll question = service.getQuestion(SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS, 0, true);
        Assert.assertEquals(3, question.getAnswers().size());
    }

    @Test
    public void shouldGetQuestionWithNoAnwers() {
        QuestionAll question = service.getQuestion(SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS, 0, false);
        Assert.assertNull(question.getAnswers());
    }


    private static List<Question> getQuestions(int size) {
        List<Question> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(Question.builder().id(i).build());
        }
        return result;
    }

    private static List<Answer> getAnswers(int size, int questionId, String surveyId) {
        List<Answer> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(Answer.builder().answerText("Answer" + 1).id("id_" + i).questionId(questionId).surveyId(surveyId).build());
        }
        return result;
    }

}
