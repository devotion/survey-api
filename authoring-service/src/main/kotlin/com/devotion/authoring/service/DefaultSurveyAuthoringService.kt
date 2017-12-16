package com.devotion.authoring.service

import com.devotion.authoring.ValidationException
import com.devotion.authoring.dto.*
import com.devotion.authoring.model.Answer
import com.devotion.authoring.model.Question
import com.devotion.authoring.model.Survey
import org.modelmapper.ModelMapper
import org.modelmapper.TypeToken
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Service
class DefaultSurveyAuthoringService(@Autowired private val surveyRepository: SurveyRepository,
                                    @Autowired val answerRepository: AnswerRepository,
                                    @Autowired val modelMapper: ModelMapper) : SurveyAuthoringService {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val questionDtoListType = object : TypeToken<List<QuestionIdAndText>>() {}.type
    private val answersDtoListType = object : TypeToken<List<AnswerIdAndText>>() {}.type

    override fun addQuestion(@NotEmpty surveyId: String, @Valid question: QuestionText): Question {
        val survey = validate(surveyRepository.findById(surveyId))
        val questions = survey.questions
        val newQuestion = modelMapper.map(question, Question::class.java)
        newQuestion.id = questions.size
        questions.add(newQuestion)
        surveyRepository.save(survey)
        return newQuestion
    }

    override fun updateQuestion(@NotEmpty surveyId: String, @NotNull questionId: Int, @Valid question: QuestionText) {
        val survey = validate(surveyRepository.findById(surveyId), questionId)
        survey.questions[questionId] = modelMapper.map(question, Question::class.java)
        surveyRepository.save(survey)
    }

    override fun deleteQuestion(@NotEmpty surveyId: String, @NotNull questionId: Int) {
        throw RuntimeException("not implemented")
    }

    override fun getQuestion(@NotEmpty surveyId: String, @NotNull questionId: Int, fetchAnswers: Boolean): QuestionAll {
        log.debug("Invoke get question surveyId={}, questionId={}", surveyId, questionId)
        val survey = validate(surveyRepository.findById(surveyId), questionId)
        val questions = survey.questions
        val question = questions[questionId]
        val questionDto = modelMapper.map(question, QuestionAll::class.java)
        if (fetchAnswers) {
            val answers = answerRepository.findBySurveyIdAndQuestionId(surveyId, questionId)
            if (answers.isNotEmpty()) {
                questionDto.answers = modelMapper.map(answers, answersDtoListType)
            }
        }
        return questionDto
    }

    override fun getAllQuestions(@NotEmpty surveyId: String): List<QuestionIdAndText> {
        val survey = validate(surveyRepository.findById(surveyId))
        val questions = survey.questions
        return modelMapper.map(questions, questionDtoListType)
    }

    override fun getAllAnswers(@NotEmpty surveyId: String, @NotNull questionId: Int): List<AnswerIdAndText> {
        validate(surveyRepository.findById(surveyId), questionId)
        val answers = answerRepository.findBySurveyIdAndQuestionId(surveyId, questionId)
        return modelMapper.map(answers, answersDtoListType)
    }

    override fun deleteAnswer(@NotEmpty answerId: String) {
        answerRepository.deleteById(answerId)
    }

    override fun updateAnswer(@NotEmpty answerId: String, @Valid answer: AnswerText) {
        val existingOne = answerRepository.findById(answerId).get()
        existingOne.answerText = answer.answerText
        answerRepository.save(existingOne)
    }

    override fun addAnswer(@NotEmpty surveyId: String, @NotNull questionId: Int, @Valid answer: AnswerText): Answer {
        validate(surveyRepository.findById(surveyId), questionId)
        val newAnswer = modelMapper.map(answer, Answer::class.java)
        newAnswer.surveyId = surveyId
        newAnswer.questionId = questionId
        return answerRepository.insert(newAnswer)
    }

    // todo: implement better and more flexible/reusable validation by using spring custom validators
    private fun validate(input: Optional<Survey>): Survey {
        if (!input.isPresent) {
            throw ValidationException(String.format("Survey [%s] could not be found", input))
        }
        val result = input.get()
        if (result.published!!) {
            throw ValidationException(String.format("Survey [%s] is already published", input))
        }
        return result
    }

    private fun validate(input: Optional<Survey>, questionId: Int): Survey {
        val survey = validate(input)
        val questions = survey.questions ?: throw ValidationException(String.format("Survey [%s] has no questions", survey))
        if (questions.size < questionId) {
            throw ValidationException(String.format("Survey [%s] has no question with id {%s}", survey, questionId))
        }
        return survey
    }
}
