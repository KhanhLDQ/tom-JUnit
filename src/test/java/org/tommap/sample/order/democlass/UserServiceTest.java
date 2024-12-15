package org.tommap.sample.order.democlass;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

@Order(1)
class UserServiceTest {
    @Test
    void userServiceTest() {
        System.out.println("Executing user service test...");
    }
}
