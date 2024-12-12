package org.tommap.learnmockito.data;

import org.tommap.learnmockito.model.MockitoUser;

public interface MockitoUserRepository {
    boolean save(MockitoUser user);
}
