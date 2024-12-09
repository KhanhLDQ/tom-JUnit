package sample;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tommap.sample.Calculator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class CalculatorTest {
    Calculator calculator;

    @BeforeAll //run once before all tests
    static void setup() {
        System.out.println("Executing @BeforeAll setup...");
    }

    @BeforeEach //run before each test
    void beforeEachTest() {
        System.out.println("Executing @BeforeEach setup...");
        calculator = new Calculator(); //clean state
    }


    //naming convention - test<MethodUnderTest>_<Scenario>_<ExpectedBehavior>
    @Test
    @DisplayName("Test 10/2=5")
    void testIntegerDivision_WhenTenIsDividedByTwo_ShouldReturnFive() {
        System.out.println("Executing Test 10/2=5...");

        //arrange
        int dividend = 10;
        int divisor = 2;
        int expectedResult = 5;

        //act
        int actualResult = calculator.integerDivision(dividend, divisor);

        //assert
        assertEquals(expectedResult, actualResult,
                () -> dividend + "/" + divisor + " did not produce " + expectedResult //using lambda to defer string concatenation - only computed if test fails
        );
    }

    @Test
    @DisplayName("Test 10/0 throws exception")
//    @Disabled("Test not yet implemented...")
    void testIntegerDivision_WhenTenIsDividedByZero_ShouldThrowArithmeticException() {
        System.out.println("Executing Test 10/0 throws exception...");

        //arrange
        int dividend = 10;
        int divisor = 0;
        String expectedExceptionMessage = "/ by zero";

        //act & assert
        ArithmeticException actualException = assertThrows(ArithmeticException.class, () -> {
            calculator.integerDivision(dividend, divisor);
        }, "Divided by zero should throw ArithmeticException...");

        //assert
        assertEquals(expectedExceptionMessage, actualException.getMessage(), "Unexpected exception message...");
    }

    @Test
    @DisplayName("Test 10-5=5")
    void testIntegerSubtraction_WhenTenIsSubtractedByFive_ShouldReturnFive() {
        System.out.println("Executing Test 10-5=5...");

        //arrange
        int minuend = 10;
        int subtrahend = 5;
        int expectedResult = 5;

        //act
        int actualResult = calculator.integerSubtraction(minuend, subtrahend);

        //assert
        assertEquals(expectedResult, actualResult,
                () -> minuend + "-" + subtrahend + " did not produce " + expectedResult
        );
    }

    @AfterEach //run after each test
    void afterEachTest() {
        System.out.println("Executing @AfterEach cleanup...");
    }

    @AfterAll //runs once after all tests complete
    static void cleanup() {
        System.out.println("Executing @AfterAll cleanup...");
    }
}
