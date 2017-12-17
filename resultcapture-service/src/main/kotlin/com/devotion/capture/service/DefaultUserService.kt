package com.devotion.capture.service

import com.devotion.capture.model.AnonimousUser
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class DefaultUserService : UserService {

    override fun resolveUser(request: HttpServletRequest): AnonimousUser {
        return AnonimousUser(request.remoteAddr)
    }

}
