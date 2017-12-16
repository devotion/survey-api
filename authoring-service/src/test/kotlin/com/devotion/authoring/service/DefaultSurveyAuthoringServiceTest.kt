package com.devotion.authoring.service

import com.devotion.authoring.ValidationException
import com.devotion.authoring.dto.QuestionText
import com.devotion.authoring.model.Answer
import com.devotion.authoring.model.Question
import com.devotion.authoring.model.Survey
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations.initMocks
import org.modelmapper.ModelMapper
import java.util.*

class DefaultSurveyAuthoringServiceTest {

    @Mock
    private lateinit var surveyRepository: SurveyRepository
    @Mock
    private lateinit var answerRepository: AnswerRepository

    private val modelMapper = ModelMapper()

    private lateinit var service: DefaultSurveyAuthoringService

    @Before
    fun setup() {
        initMocks(this)
        service = DefaultSurveyAuthoringService(surveyRepository, answerRepository, modelMapper)
        `when`(surveyRepository.findById(SURVEY_ID_0_PUBLISHED)).thenReturn(Optional.of(Survey(published = true)))
        `when`(surveyRepository.findById(SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS)).thenReturn(Optional.of(Survey(published = false, questions = getQuestions(4))))
        `when`(surveyRepository.findById(SURVEY_ID_2_NON_EXISTING)).thenReturn(Optional.empty())
        `when`(surveyRepository.findById(SURVEY_ID_3_NON_PUBLISHED_WITH_1_QUESTION)).thenReturn(Optional.of(Survey(published = false, questions = getQuestions(1))))
        `when`(surveyRepository.findById(SURVEY_ID_4_NON_PUBLISHED_WITH_NO_QUESTIONS)).thenReturn(Optional.of(Survey(published = false)))
        `when`(answerRepository.findBySurveyIdAndQuestionId(SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS, 0)).thenReturn(getAnswers(3, 0, SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS))
    }

    @Test
    fun shouldAddQuestion() {
        val newQuestion = service.addQuestion(SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS, QuestionText(questionText = "This is new question"))
        Assert.assertEquals(Integer.valueOf(4), newQuestion.id)
    }

    @Test(expected = ValidationException::class)
    fun failAddQuestionForNonExistingSurvey() {
        service.addQuestion(SURVEY_ID_2_NON_EXISTING, QuestionText("This is new question"))
    }

    @Test(expected = ValidationException::class)
    fun failAddQuestionForPublishedSurvey() {
        service.addQuestion(SURVEY_ID_0_PUBLISHED, QuestionText("This is new question"))
    }

    @Test
    fun shouldUpdateQuestion() {
        val newQuestion = service.addQuestion("1", QuestionText("This is new question"))
        val map = modelMapper.map(newQuestion, QuestionText::class.java)
        map.questionText = "Updated question text"
        service.updateQuestion(SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS, 4, map)
        Assert.assertEquals("Updated question text", map.questionText)
    }

    @Test(expected = ValidationException::class)
    fun failUpdateQuestionForNonExistingSurvey() {
        service.updateQuestion(SURVEY_ID_2_NON_EXISTING, 1, QuestionText("This is new question"))
    }

    @Test(expected = ValidationException::class)
    fun failUpdateQuestionForPublishedSurvey() {
        service.updateQuestion(SURVEY_ID_0_PUBLISHED, 1, QuestionText("This is new question"))
    }

    @Test(expected = ValidationException::class)
    fun failUpdateNonExistingQuestion() {
        service.updateQuestion(SURVEY_ID_3_NON_PUBLISHED_WITH_1_QUESTION, 4, QuestionText("This is new question"))
    }

    @Ignore
    @Test
    fun shouldDeleteQuestion() {
        val questions = getQuestions(3)
        val survey = Survey(id = "5", questions = questions, published = false)
        `when`(surveyRepository.findById("5")).thenReturn(Optional.of(survey))
        service.deleteQuestion("5", 2)
        Assert.assertEquals(2, questions.size.toLong())
    }

    @Test
    fun shouldGetAllQuestions() {
        val allQuestions = service.getAllQuestions(SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS)
        Assert.assertEquals(4, allQuestions.size.toLong())
    }

    @Test
    fun shouldReturnNullWhenNoQuestionsInSurvey() {
        Assert.assertEquals(service.getAllQuestions(SURVEY_ID_4_NON_PUBLISHED_WITH_NO_QUESTIONS).size, 0)
    }

    @Test(expected = ValidationException::class)
    fun failGetAllQuestionsForNonExistingSurvey() {
        service.getAllQuestions(SURVEY_ID_2_NON_EXISTING)
    }

    @Test
    fun shouldGetQuestionWithAnswers() {
        val question = service.getQuestion(SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS, 0, true)
        Assert.assertEquals(3, question.answers!!.size.toLong())
    }

    @Test
    fun shouldGetQuestionWithNoAnswers() {
        val question = service.getQuestion(SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS, 0, false)
        Assert.assertNull(question.answers)
    }

    companion object {
        val SURVEY_ID_0_PUBLISHED = "0"
        val SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS = "1"
        val SURVEY_ID_2_NON_EXISTING = "2"
        val SURVEY_ID_3_NON_PUBLISHED_WITH_1_QUESTION = "3"
        private val SURVEY_ID_4_NON_PUBLISHED_WITH_NO_QUESTIONS = "4"

        private fun getQuestions(size: Int): MutableList<Question> {
            val result = ArrayList<Question>(size)
            for (i in 0 until size) {
                result.add(Question(id = i))
            }
            return result
        }

        private fun getAnswers(size: Int, questionId: Int, surveyId: String): List<Answer> {
            val result = ArrayList<Answer>(size)
            for (i in 0 until size) {
                result.add(Answer(answerText = "Answer" + 1, id = "id_" + i, questionId = questionId, surveyId = surveyId))
            }
            return result
        }
    }
}
