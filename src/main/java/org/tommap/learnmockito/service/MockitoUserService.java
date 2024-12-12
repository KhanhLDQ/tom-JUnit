package org.tommap.learnmockito.service;

import org.tommap.learnmockito.model.MockitoUser;

public interface MockitoUserService {
    MockitoUser createUser(
            String firstName,
            String lastName,
            String email,
            String password,
            String repeatedPassword
    );
}
