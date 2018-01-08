package com.devotion.authoring.service

import com.devotion.authoring.ValidationException
import com.devotion.authoring.dto.QuestionText
import com.devotion.authoring.model.Answer
import com.devotion.authoring.model.Question
import com.devotion.authoring.model.Survey
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
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
        with(surveyRepository) {
            `when`(findById(SURVEY_ID_0_PUBLISHED)).thenReturn(Optional.of(Survey(published = true)))
            `when`(findById(SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS)).thenReturn(Optional.of(Survey(published = false, questions = getQuestions(4))))
            `when`(findById(SURVEY_ID_2_NON_EXISTING)).thenReturn(Optional.empty())
            `when`(findById(SURVEY_ID_3_NON_PUBLISHED_WITH_1_QUESTION)).thenReturn(Optional.of(Survey(published = false, questions = getQuestions(1))))
            `when`(findById(SURVEY_ID_4_NON_PUBLISHED_WITH_NO_QUESTIONS)).thenReturn(Optional.of(Survey(published = false)))
        }
        `when`(answerRepository.findBySurveyIdAndQuestionId(SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS, 0)).thenReturn(getAnswers(3, 0, SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS))
    }

    @Test
    fun `should add question`() {
        val newQuestion = service.addQuestion(SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS, QuestionText(questionText = "This is new question"))
        assertThat(newQuestion.id).isEqualTo(4)
    }

    @Test
    fun `fails adding a question for non existing survey`() {
        assertThatThrownBy {
            service.addQuestion(SURVEY_ID_2_NON_EXISTING, QuestionText("This is new question"))
        }.isExactlyInstanceOf(ValidationException::class.java)
    }

    @Test
    fun `fails adding question for published survey`() {
        assertThatThrownBy {
            service.addQuestion(SURVEY_ID_0_PUBLISHED, QuestionText("This is new question"))
        }.isExactlyInstanceOf(ValidationException::class.java)
    }

    @Test
    fun `should update question`() {
        val newQuestion = service.addQuestion("1", QuestionText("This is new question"))
        val map = modelMapper.map(newQuestion, QuestionText::class.java)
        map.questionText = "Updated question text"
        service.updateQuestion(SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS, 4, map)
        assertThat(map.questionText).isEqualTo("Updated question text")
    }

    @Test
    fun `fails to update question for non existing survey`() {
        assertThatThrownBy {
            service.updateQuestion(SURVEY_ID_2_NON_EXISTING, 1, QuestionText("This is new question"))
        }.isExactlyInstanceOf(ValidationException::class.java)
    }

    @Test
    fun `fails to update question for published survey`() {
        assertThatThrownBy {
            service.updateQuestion(SURVEY_ID_0_PUBLISHED, 1, QuestionText("This is new question"))
        }.isExactlyInstanceOf(ValidationException::class.java)
    }

    @Test
    fun `fails to update non existing question`() {
        assertThatThrownBy {
            service.updateQuestion(SURVEY_ID_3_NON_PUBLISHED_WITH_1_QUESTION, 4, QuestionText("This is new question"))
        }.isExactlyInstanceOf(ValidationException::class.java)
    }

    @Ignore
    @Test
    fun `should delete question`() {
        val questions = getQuestions(3)
        val survey = Survey(id = "5", questions = questions, published = false)
        `when`(surveyRepository.findById("5")).thenReturn(Optional.of(survey))
        service.deleteQuestion("5", 2)
        assertThat(questions.size).isEqualTo(2)
    }

    @Test
    fun `should get all questions`() {
        val allQuestions = service.getAllQuestions(SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS)
        assertThat(allQuestions.size).isEqualTo(4)
    }

    @Test
    fun `should return null when no questions in survey`() {
        assertThat(service.getAllQuestions(SURVEY_ID_4_NON_PUBLISHED_WITH_NO_QUESTIONS).size).isEqualTo(0)
    }

    @Test
    fun `fail get all questions for non existing survey`() {
        assertThatThrownBy {
            service.getAllQuestions(SURVEY_ID_2_NON_EXISTING)
        }.isExactlyInstanceOf(ValidationException::class.java)
    }

    @Test
    fun `should get question with answers`() {
        val question = service.getQuestion(SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS, 0, true)
        assertThat(question.answers!!.size).isEqualTo(3)
    }

    @Test
    fun `should get question with no answers`() {
        val question = service.getQuestion(SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS, 0, false)
        assertThat(question.answers).isNull()
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
