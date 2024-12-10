package sample;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.tommap.sample.Calculator;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @RepeatedTest(value = 3, name = "{displayName} - repetition {currentRepetition} of {totalRepetitions}") //make sure behavior is consistent even executing multiple times
    @DisplayName("Test 10/2=5 with @RepeatedTest")
    void testIntegerDivision_WhenUsingRepeatedTestAnnotation(
            RepetitionInfo repetitionInfo,
            TestInfo testInfo
    ) {
        System.out.println("Running Test: " + testInfo.getTestMethod().get().getName());
        System.out.println("Repetition #" + repetitionInfo.getCurrentRepetition() + " of " + repetitionInfo.getTotalRepetitions());

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

    @ParameterizedTest
    @ValueSource(strings = {"Tom", "Jerry", "Khanh"}) //only one argument
    @DisplayName("Test value source annotation")
    void valueSourceAnnotation(String name) {
        System.out.println("Executing Test " + name + "...");

        //arrange
        //act

        //assert
        assertNotNull(name);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/integerSubtraction.csv")
    @DisplayName("Test integer subtraction using @CsvFileSource")
    void testIntegerSubtraction_WhenUsingCsvFileSourceAnnotation(
            int minuend, int subtrahend, int expectedResult
    ) {
        System.out.println("Executing Test " + minuend + "-" + subtrahend + "=" + expectedResult + "...");

        //arrange

        //act
        int actualResult = calculator.integerSubtraction(minuend, subtrahend);

        //assert
        assertEquals(expectedResult, actualResult,
                () -> minuend + "-" + subtrahend + " did not produce " + expectedResult
        );
    }

    @ParameterizedTest
    @CsvSource({
            "10, 5, 5",
            "20, 5, 15",
            "8, 5, 3"
    })
    @DisplayName("Test integer subtraction using @CsvSource")
    void testIntegerSubtraction_WhenUsingCsvSourceAnnotation(
            int minuend, int subtrahend, int expectedResult
    ) {
        System.out.println("Executing Test " + minuend + "-" + subtrahend + "=" + expectedResult + "...");

        //arrange

        //act
        int actualResult = calculator.integerSubtraction(minuend, subtrahend);

        //assert
        assertEquals(expectedResult, actualResult,
                () -> minuend + "-" + subtrahend + " did not produce " + expectedResult
        );
    }

    @ParameterizedTest
    @MethodSource("integerSubtractionInputParameters")
    @DisplayName("Test integer subtraction")
    void testIntegerSubtraction_WhenTenIsSubtractedByFive_ShouldReturnFive(
            int minuend, int subtrahend, int expectedResult
    ) {
        System.out.println("Executing Test " + minuend + "-" + subtrahend + "=" + expectedResult + "...");

        //arrange

        //act
        int actualResult = calculator.integerSubtraction(minuend, subtrahend);

        //assert
        assertEquals(expectedResult, actualResult,
                () -> minuend + "-" + subtrahend + " did not produce " + expectedResult
        );
    }

    private static Stream<Arguments> integerSubtractionInputParameters() {
        return Stream.of(
                Arguments.of(10, 5, 5),
                Arguments.of(20, 5, 15),
                Arguments.of(8, 5, 3)
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
