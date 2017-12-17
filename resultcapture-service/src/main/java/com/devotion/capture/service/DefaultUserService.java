package com.devotion.capture.service;

import com.devotion.capture.model.AnonumousUser;
import com.devotion.capture.model.RegistredUser;
import com.devotion.capture.model.User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Service
public class DefaultUserService implements UserService {

    @Override
    public User resolveUser(HttpServletRequest request) {
        Principal userPrincipal = request.getUserPrincipal();
        if (userPrincipal == null) {
            return new AnonumousUser(request.getRemoteAddr());
        } else {
            return RegistredUser.builder().uniqueId(userPrincipal.getName()).build();
        }
    }

}
