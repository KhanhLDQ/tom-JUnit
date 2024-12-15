package org.tommap.sample.order.democlass;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

@Order(2)
class ProductServiceTest {
    @Test
    void productServiceTest() {
        System.out.println("Executing product service test...");
    }
}
