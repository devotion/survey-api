package com.devotion.capture.service

import com.devotion.capture.model.AnonimousUser

import javax.servlet.http.HttpServletRequest

interface UserService {

    fun resolveUser(request: HttpServletRequest): AnonimousUser

}
