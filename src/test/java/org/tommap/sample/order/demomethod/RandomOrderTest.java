package org.tommap.sample.order.demomethod;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/*
    - JUnit by default runs tests using a deterministic but unpredictable order
    - reference: https://www.baeldung.com/junit-5-test-order
 */
@TestMethodOrder(MethodOrderer.Random.class)
class RandomOrderTest {
    @Test
    void firstTest() {
        System.out.println("Executing first test...");
    }

    @Test
    void secondTest() {
        System.out.println("Executing second test...");
    }

    @Test
    void thirdTest() {
        System.out.println("Executing third test...");
    }

    @Test
    void fourthTest() {
        System.out.println("Executing fourth test...");
    }
}
