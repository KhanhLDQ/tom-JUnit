package org.tommap.learnmockito.service;

import org.tommap.learnmockito.model.MockitoUser;

public class MockitoEmailVerificationServiceImpl implements MockitoEmailVerificationService{
    @Override
    public void scheduleEmailConfirmation(MockitoUser user) {
        System.out.println("sending email confirmation");
    }
}
