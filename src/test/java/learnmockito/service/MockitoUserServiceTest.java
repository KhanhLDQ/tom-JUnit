package learnmockito.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tommap.learnmockito.data.MockitoUserRepository;
import org.tommap.learnmockito.model.MockitoUser;
import org.tommap.learnmockito.service.EmailNotificationServiceException;
import org.tommap.learnmockito.service.MockitoEmailVerificationService;
import org.tommap.learnmockito.service.MockitoUserServiceException;
import org.tommap.learnmockito.service.MockitoUserServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/*
    - test driven development (TDD) 4 steps:
        1. write a failing test (red)
        2. write the minimum amount of code to make the test pass (green)
        3. refactor the code
        4. repeat the process
 */
@ExtendWith(MockitoExtension.class)
class MockitoUserServiceTest {
    String firstName;
    String lastName;
    String email;
    String password;
    String repeatedPassword;

    @Mock
    MockitoUserRepository userRepository;
    @Mock
    MockitoEmailVerificationService emailVerificationService;

    @InjectMocks
    MockitoUserServiceImpl userService; //require implementation class instead of interface

    @BeforeEach
    void init() {
        firstName = "Tom";
        lastName = "Map";
        email = "tom@gmail.com";
        password = "123456";
        repeatedPassword = "123456";
    }

    //naming convention - test<MethodUnderTest>_<Scenario>_<ExpectedBehavior>
    @Test
    @DisplayName("User object created")
    void testCreateUser_WhenUserDetailsProvided_ShouldReturnUserObject() {
        //arrange
        when(userRepository.save(any(MockitoUser.class))).thenReturn(true);

        //act
        MockitoUser user = userService.createUser(firstName, lastName, email, password, repeatedPassword);

        //assert
        assertNotNull(user, "creatUser() should not return null");
        assertNotNull(user.id(), "id should not be null");
        assertEquals("Tom", user.firstName(), "firstName should be Tom");
        assertEquals("Map", user.lastName(), "lastName should be Map");
        assertEquals("tom@gmail.com", user.email(), "email should be tom@gmail.com");

        verify(userRepository, times(1)).save(any(MockitoUser.class));
        verify(emailVerificationService, times(1)).scheduleEmailConfirmation(any(MockitoUser.class));
    }

    @Test
    @DisplayName("First name is empty")
    void testCreateUser_WhenFirstNameIsEmpty_ShouldThrowIllegalArgumentException() {
        //arrange
        firstName = "";
        String expectedExceptionMessage = "user's first name is empty!";

        //act & assert
        IllegalArgumentException actualException = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(firstName, lastName, email, password, repeatedPassword);
        }, "should throw IllegalArgumentException when firstName is empty");

        //assert
        assertEquals(expectedExceptionMessage, actualException.getMessage(),
                "exception message should be user's first name is empty!"
        );

        verify(userRepository, never()).save(any(MockitoUser.class));
        verify(emailVerificationService, never()).scheduleEmailConfirmation(any(MockitoUser.class));
    }

    @Test
    @DisplayName("Last name is empty")
    void testCreateUser_WhenLastNameIsEmpty_ShouldThrowIllegalArgumentException() {
        //arrange
        lastName = "";
        String expectedExceptionMessage = "user's last name is empty!";

        //act & assert
        IllegalArgumentException actualException = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(firstName, lastName, email, password, repeatedPassword);
        }, "should throw IllegalArgumentException when lastName is empty");

        //assert
        assertEquals(expectedExceptionMessage, actualException.getMessage(),
                "exception message should be user's last name is empty!"
        );

        verify(userRepository, never()).save(any(MockitoUser.class));
        verify(emailVerificationService, never()).scheduleEmailConfirmation(any(MockitoUser.class));
    }

    @Test
    @DisplayName("Save user throws exception")
    void testCreateUser_WhenSaveUserThrowsException_ShouldThrowMockitoUserServiceException() {
        //arrange
        when(userRepository.save(any(MockitoUser.class))).thenThrow(RuntimeException.class);

        //act & assert
        assertThrows(MockitoUserServiceException.class, () -> {
            userService.createUser(firstName, lastName, email, password, repeatedPassword);
        }, "should throw MockitoUserServiceException when save() throws exception");

        //assert
        verify(userRepository, times(1)).save(any(MockitoUser.class));
        verify(emailVerificationService, never()).scheduleEmailConfirmation(any(MockitoUser.class));
    }

    @Test
    @DisplayName("Save user returns false")
    void testCreateUser_WhenSaveUserReturnsFalse_ShouldThrowMockitoUserServiceException() {
        //arrange
        when(userRepository.save(any(MockitoUser.class))).thenReturn(false);
        String expectedExceptionMessage = "could not create user!";

        //act & assert
        MockitoUserServiceException actualException = assertThrows(MockitoUserServiceException.class, () -> {
            userService.createUser(firstName, lastName, email, password, repeatedPassword);
        }, "should throw MockitoUserServiceException when save() returns false");

        //assert
        verify(userRepository, times(1)).save(any(MockitoUser.class));
        verify(emailVerificationService, never()).scheduleEmailConfirmation(any(MockitoUser.class));

        assertEquals(expectedExceptionMessage, actualException.getMessage(),
                "exception message should be could not create user!"
        );
    }

    @Test
    @DisplayName("Schedule email confirmation throws exception")
    void testCreateUser_WhenScheduleEmailConfirmationThrowsException_ShouldThrowMockitoUserServiceException() {
        //arrange
        when(userRepository.save(any(MockitoUser.class))).thenReturn(true);
        doThrow(EmailNotificationServiceException.class)
                .when(emailVerificationService).scheduleEmailConfirmation(any(MockitoUser.class)); //stub void methods

        //act & assert
        assertThrows(MockitoUserServiceException.class, () -> {
            userService.createUser(firstName, lastName, email, password, repeatedPassword);
        }, "should throw MockitoUserServiceException when scheduleEmailConfirmation() throws exception");

        //assert
        verify(userRepository, times(1)).save(any(MockitoUser.class));
        verify(emailVerificationService, times(1)).scheduleEmailConfirmation(any(MockitoUser.class));
    }
}
