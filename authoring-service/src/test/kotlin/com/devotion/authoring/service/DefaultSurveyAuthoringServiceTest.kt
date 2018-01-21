package com.devotion.authoring.service


import com.devotion.authoring.KafkaConfig
import com.devotion.authoring.ValidationException
import com.devotion.authoring.dto.AddQuestionEvent
import com.devotion.authoring.dto.DeleteQuestionEvent
import com.devotion.authoring.dto.QuestionText
import com.devotion.authoring.dto.UpdateQuestionEvent
import com.devotion.authoring.model.Question
import com.devotion.authoring.model.Survey
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations.initMocks
import org.modelmapper.ModelMapper
import org.springframework.kafka.core.KafkaTemplate
import java.util.*


var questionText = QuestionText("This is first question")


class DefaultSurveyAuthoringServiceTest {

    @Mock
    private lateinit var surveyRepository: SurveyRepository

    @Mock
    private lateinit var answerRepository: AnswerRepository

    @Mock
    private lateinit var kafkaTemplate: KafkaTemplate<String, String>

    @Mock
    private lateinit var kafkaConfig: KafkaConfig

    private val modelMapper = ModelMapper()

    private lateinit var service: DefaultSurveyAuthoringService

    var survey_0 = Optional.of(Survey(published = true))
    var survey_1 = Optional.of(Survey(published = false).apply { questions.addAll(DefaultSurveyAuthoringServiceTest.getQuestions(4))})
    var survey_2 = Optional.empty<Survey>()
    var survey_3 = Optional.of(Survey(published = false).apply { questions.addAll(DefaultSurveyAuthoringServiceTest.getQuestions(1))})
    val survey_4 = Optional.of(Survey(published = false))

    @Before
    fun setup() {
        initMocks(this)
        service = DefaultSurveyAuthoringService(surveyRepository, answerRepository, modelMapper, kafkaConfig, kafkaTemplate, emptyMap())
        `when`(surveyRepository.findById(SURVEY_ID_0_PUBLISHED)).thenReturn(survey_0)
        `when`(surveyRepository.findById(SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS)).thenReturn(survey_1)
        `when`(surveyRepository.findById(SURVEY_ID_2_NON_EXISTING)).thenReturn(survey_2)
        `when`(surveyRepository.findById(SURVEY_ID_3_NON_PUBLISHED_WITH_1_QUESTION)).thenReturn(survey_3)
        `when`(surveyRepository.findById(SURVEY_ID_4_NON_PUBLISHED_WITH_NO_QUESTIONS)).thenReturn(survey_4)
        //`when`(answerRepository.findBySurveyIdAndQuestionId(SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS, 0)).thenReturn(getAnswers(3, 0, SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS))
    }

    @Test
    fun `should produce add question event`() {
        service.addQuestion(SURVEY_ID_3_NON_PUBLISHED_WITH_1_QUESTION, questionText)
        assertThat(survey_3.get().questions.size).isEqualTo(1)
    }

    @Test
    fun `should process add question event`() {
        service.onModifyQuestionEvent(AddQuestionEvent(SURVEY_ID_3_NON_PUBLISHED_WITH_1_QUESTION, questionText));
        assertThat(survey_3.get().questions.size).isEqualTo(2)
    }

    @Test
    fun `fail processing add question event for non-existing survey`() {
        try {
            service.onModifyQuestionEvent(AddQuestionEvent(SURVEY_ID_2_NON_EXISTING, questionText))
            fail("Error not thrown")
        } catch (ex: ValidationException) {
            assertThat(ex.messages.size).isEqualTo(1)
            assertThat(ex.messages).contains("Survey with id [$SURVEY_ID_2_NON_EXISTING] could not be found")
        }
    }

    @Test
    fun `fail processing add question event for published survey`() {
        try {
            service.onModifyQuestionEvent(AddQuestionEvent( SURVEY_ID_0_PUBLISHED,  questionText))
            fail("Error not thrown")
        } catch (ex: ValidationException) {
            assertThat(ex.messages.size).isEqualTo(1)
            assertThat(ex.messages).contains("Survey [$SURVEY_ID_0_PUBLISHED] is already published")
        }
    }

    @Test
    fun `fail processing add question event for question without text`() {
        try {
            service.onModifyQuestionEvent(AddQuestionEvent( SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS, QuestionText()))
            fail("Error not thrown")
        } catch (ex: ValidationException) {
            assertThat(ex.messages.size).isEqualTo(1)
            assertThat(ex.messages).contains("Question text can not be empty.")
        }
    }

    @Test
    fun `should produce update question event`() {
        service.updateQuestion(SURVEY_ID_3_NON_PUBLISHED_WITH_1_QUESTION, survey_3.get().questions.get(0).id, questionText)
    }

    @Test
    fun `should process update question event`() {
        val question = survey_3.get().questions[0]
        val originalQuestionText = question.questionText
        val originalQuestionId = question.id
        service.onModifyQuestionEvent(UpdateQuestionEvent( SURVEY_ID_3_NON_PUBLISHED_WITH_1_QUESTION, originalQuestionId, questionText))
        assertThat(survey_3.get().questions[0].questionText).isNotEqualTo(originalQuestionText)
        assertThat(survey_3.get().questions[0].questionText).isEqualTo(questionText.questionText)
        assertThat(survey_3.get().questions[0].id).isEqualTo(originalQuestionId)
    }

    @Test
    fun `fail process update question for non existing survey`() {
        try {
            service.onModifyQuestionEvent(UpdateQuestionEvent( SURVEY_ID_2_NON_EXISTING, "IT SHOULD FAIL BEFORE Checking THIS", questionText))
            fail("Error not thrown")
        } catch (ex: ValidationException) {
            assertThat(ex.messages.size).isEqualTo(1)
            assertThat(ex.messages).contains("Survey with id [$SURVEY_ID_2_NON_EXISTING] could not be found")
        }
    }

    @Test
    fun `fail process update question for published survey`() {
        try {
            service.onModifyQuestionEvent(UpdateQuestionEvent( SURVEY_ID_0_PUBLISHED, "IT SHOULD FAIL BEFORE Checking THIS", questionText))
            fail("Error not thrown")
        } catch (ex: ValidationException) {
            assertThat(ex.messages.size).isEqualTo(1)
            assertThat(ex.messages).contains("Survey [$SURVEY_ID_0_PUBLISHED] is already published")
        }
    }

    @Test
    fun `fail process update question for non existing question id and empty question text`() {
        try {
            service.onModifyQuestionEvent(UpdateQuestionEvent( SURVEY_ID_3_NON_PUBLISHED_WITH_1_QUESTION, "IT SHOULD FAIL on This", QuestionText("")))
            fail("Error not thrown")
        } catch (ex: ValidationException) {
            assertThat(ex.messages.size).isEqualTo(2)
            assertThat(ex.messages).contains("Question id is not valid.")
            assertThat(ex.messages).contains("Question text can't be null or empty.")
        }
    }

    @Test
    fun `should produce delete question event`() {
        service.deleteQuestion(SURVEY_ID_3_NON_PUBLISHED_WITH_1_QUESTION, "2")
    }

    @Test
    fun `should process delete question event`() {
        val question = survey_1.get().questions[2];
        service.onModifyQuestionEvent(DeleteQuestionEvent( SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS, question.id))
        assertThat(survey_1.get().questions).doesNotContain(question)
    }

    @Test
    fun `fail process delete event for non existing question id`() {
        try {
            service.onModifyQuestionEvent(DeleteQuestionEvent( SURVEY_ID_3_NON_PUBLISHED_WITH_1_QUESTION, "IT SHOULD FAIL on This"))
            fail("Error not thrown")
        } catch (ex: ValidationException) {
            assertThat(ex.messages.size).isEqualTo(1)
            assertThat(ex.messages).contains("Question id is not valid.")
        }
    }

    @Test
    fun `fail process delete event for non existing survey`() {
        try {
            service.onModifyQuestionEvent(DeleteQuestionEvent( SURVEY_ID_2_NON_EXISTING, "IT SHOULD FAIL before"))
            fail("Error not thrown")
        } catch (ex: ValidationException) {
            assertThat(ex.messages.size).isEqualTo(1)
            assertThat(ex.messages).contains("Survey with id [$SURVEY_ID_2_NON_EXISTING] could not be found")
        }
    }

    @Test
    fun `fail process delete event for published survey`() {
        try {
            service.onModifyQuestionEvent(DeleteQuestionEvent( SURVEY_ID_0_PUBLISHED, "IT SHOULD FAIL before"))
            fail("Error not thrown")
        } catch (ex: ValidationException) {
            assertThat(ex.messages.size).isEqualTo(1)
            assertThat(ex.messages).contains("Survey [$SURVEY_ID_0_PUBLISHED] is already published")
        }
    }

    @Test
    fun shouldGetAllQuestions() {
        val allQuestions = service.getAllQuestions(SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS)
        assertThat(allQuestions.size).isEqualTo(4)
    }

    @Test
    fun shouldReturnNullWhenNoQuestionsInSurvey() {
        Assert.assertEquals(service.getAllQuestions(SURVEY_ID_4_NON_PUBLISHED_WITH_NO_QUESTIONS).size, 0)
    }

    @Test(expected = ValidationException::class)
    fun failGetAllQuestionsForNonExistingSurvey() {
        service.getAllQuestions(SURVEY_ID_2_NON_EXISTING)
    }

    companion object {
        val SURVEY_ID_0_PUBLISHED = "0"
        val SURVEY_ID_1_NON_PUBLISHED_WITH_4_QUESTIONS = "1"
        val SURVEY_ID_2_NON_EXISTING = "2"
        val SURVEY_ID_3_NON_PUBLISHED_WITH_1_QUESTION = "3"
        val SURVEY_ID_4_NON_PUBLISHED_WITH_NO_QUESTIONS = "4"

        fun getQuestions(size: Int): List<Question> {
            return (0 until size).map { Question(it.toString()).apply { id = it.toString() } }
        }
    }
}
