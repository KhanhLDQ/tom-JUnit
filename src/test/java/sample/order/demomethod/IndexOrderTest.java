package sample.order.demomethod;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

/*
    - JUnit by default runs tests using a deterministic but unpredictable order
    - reference: https://www.baeldung.com/junit-5-test-order

    - JUnit by default creates a new test instance for each test method
 */
//@TestInstance(TestInstance.Lifecycle.PER_METHOD) //default behavior
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//share the same test class instance - @BeforeAll | @AfterAll do not need to be static
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IndexOrderTest {
    StringBuilder state = new StringBuilder();

    @AfterEach
    void afterEach() {
        System.out.println("State: " + state.toString());
    }

    @Test
    @Order(1)
    void firstTest() {
        System.out.println("Executing first test...");
        state.append("1");
    }

    @Test
    @Order(2)
    void secondTest() {
        System.out.println("Executing second test...");
        state.append("2");
    }

    @Test
    @Order(3)
    void thirdTest() {
        System.out.println("Executing third test...");
        state.append("3");
    }

    @Test
    @Order(4)
    void fourthTest() {
        System.out.println("Executing fourth test...");
        state.append("4");
    }
}
