package org.tommap.learnmockito.service;

import org.tommap.learnmockito.model.MockitoUser;

public interface MockitoEmailVerificationService {
    void scheduleEmailConfirmation(MockitoUser user);
}
