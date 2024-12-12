package org.tommap.learnmockito.service;

import org.tommap.learnmockito.data.MockitoUserRepository;
import org.tommap.learnmockito.model.MockitoUser;

import java.util.UUID;

public class MockitoUserServiceImpl implements MockitoUserService {
    private final MockitoUserRepository userRepository;
    private final MockitoEmailVerificationService emailVerificationService;

    public MockitoUserServiceImpl(MockitoUserRepository userRepository, MockitoEmailVerificationService emailVerificationService) {
        this.userRepository = userRepository;
        this.emailVerificationService = emailVerificationService;
    }

    @Override
    public MockitoUser createUser(String firstName, String lastName, String email, String password, String repeatedPassword) {
        if (null == firstName || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("user's first name is empty!");
        }

        if (null == lastName || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("user's last name is empty!");
        }

        MockitoUser user = new MockitoUser(UUID.randomUUID().toString(), firstName, lastName, email);

        boolean isSaved;

        try {
            isSaved = userRepository.save(user);
        } catch (RuntimeException ex) {
            throw new MockitoUserServiceException(ex.getMessage());
        }

        if (!isSaved) {
            throw new MockitoUserServiceException("could not create user!");
        }

        try {
            emailVerificationService.scheduleEmailConfirmation(user);
        } catch (EmailNotificationServiceException ex) {
            throw new MockitoUserServiceException(ex.getMessage());
        }

        return user;
    }
}
