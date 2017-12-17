package com.devotion.capture.service;

import com.devotion.capture.model.User;

import javax.servlet.http.HttpServletRequest;

public interface UserService {

    User resolveUser(HttpServletRequest request);

}
