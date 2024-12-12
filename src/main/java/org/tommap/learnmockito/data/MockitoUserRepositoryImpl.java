package org.tommap.learnmockito.data;

import org.tommap.learnmockito.model.MockitoUser;

import java.util.HashMap;
import java.util.Map;

public class MockitoUserRepositoryImpl implements MockitoUserRepository {
    Map<String, Object> users = new HashMap<>();

    @Override
    public boolean save(MockitoUser user) {
        boolean isSaved = false;

        if (!users.containsKey(user.id())) {
            users.put(user.id(), user);
            isSaved = true;
        }

        return isSaved;
    }
}
