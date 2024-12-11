package sample.order.demomethod;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/*
    - JUnit by default runs tests using a deterministic but unpredictable order
    - reference: https://www.baeldung.com/junit-5-test-order
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IndexOrderTest {
    @Test
    @Order(1)
    void firstTest() {
        System.out.println("Executing first test...");
    }

    @Test
    @Order(2)
    void secondTest() {
        System.out.println("Executing second test...");
    }

    @Test
    @Order(3)
    void thirdTest() {
        System.out.println("Executing third test...");
    }

    @Test
    @Order(4)
    void fourthTest() {
        System.out.println("Executing fourth test...");
    }
}
