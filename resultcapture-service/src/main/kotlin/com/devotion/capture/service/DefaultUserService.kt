package com.devotion.capture.service

import com.devotion.capture.model.AnonymousUser
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class DefaultUserService : UserService {

    override fun resolveUser(request: HttpServletRequest): AnonymousUser {
        return AnonymousUser(request.remoteAddr)
    }

}
