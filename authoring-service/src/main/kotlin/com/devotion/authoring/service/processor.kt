package com.devotion.authoring.service

import com.devotion.authoring.dto.AddQuestionEvent
import com.devotion.authoring.dto.Event
import org.springframework.stereotype.Component

interface Proc<in E : Event> {
    fun process(event: E)
}

@Component("AddQuestionEvent")
class ProcImpl : Proc<AddQuestionEvent> {
    override fun process(event: AddQuestionEvent) {
    }
}