package com.devotion.authoring.service

import com.devotion.authoring.model.Survey
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface SurveyRepository : MongoRepository<Survey, String>
