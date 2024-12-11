package sample.order.democlass;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

@Order(3)
class OrderServiceTest {
    @Test
    void orderServiceTest() {
        System.out.println("Executing order service test...");
    }
}
