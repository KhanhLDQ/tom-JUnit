package org.tommap.io;

import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
    - @DataJpaTest
        + create application context with JPA-related components only
        + test method by default is transactional and will roll back when completes
        + in-memory databased by default is used
 */
@DataJpaTest
@TestPropertySource(locations = "/application-test.properties")
class UserEntityIntegrationTest {
    @Autowired
    private TestEntityManager testEntityManager;

    UserEntity userEntity;

    @BeforeEach
    void init() {
        userEntity = new UserEntity();
        userEntity.setUserId("tom_user_id");
        userEntity.setFirstName("tom");
        userEntity.setLastName("map");
        userEntity.setEmail("tom@gmail.com");
        userEntity.setEncryptedPassword("12345678");
    }

    @Test
    @DisplayName("store user success")
    @Order(1)
    void testUserEntity_WhenValidUserDetailsProvided_ShouldReturnStoredData() {
        //arrange

        //act
        UserEntity storedUser = testEntityManager.persistAndFlush(userEntity); //rollback after test completes

        //assert
        assertTrue(storedUser.getId() > 0L);
        assertEquals("tom_user_id", storedUser.getUserId(), "UserId data is incorrect");
        assertEquals("tom", storedUser.getFirstName(), "FirstName data is incorrect");
        assertEquals("map", storedUser.getLastName(), "LastName data is incorrect");
        assertEquals("tom@gmail.com", storedUser.getEmail(), "Email data is incorrect");
        assertEquals("12345678", storedUser.getEncryptedPassword(), "EncryptedPassword data is incorrect");
    }

    @ParameterizedTest(name = "[{index}] - {1}")
    @MethodSource("invalidFirstNameCases")
    @DisplayName("invalid first name provided")
    void testUserEntity_WhenInvalidFirstNameProvided_ShouldThrowPersistenceException(
            String firstName, String description
    ) {
        //arrange
        userEntity.setFirstName(firstName);

        //act & assert
        PersistenceException expectedPersistenceExceptionToBeThrown = assertThrows(PersistenceException.class, () -> {
            testEntityManager.persistAndFlush(userEntity);
        }, "Expected PersistenceException to be thrown");
        System.out.println(expectedPersistenceExceptionToBeThrown.getMessage());
    }

    private static Stream<Arguments> invalidFirstNameCases() {
        return Stream.of(
                Arguments.of(null, "FirstName is null"),
                Arguments.of("This is a string that contains more than fifty characters.", "FirstName is longer than 50 characters")
        );
    }

    @Test
    @DisplayName("duplicated user id")
    void testUserEntity_WhenUserIdDuplicated_ShouldThrowPersistenceException() {
        //arrange
        UserEntity newUser = new UserEntity();
        newUser.setUserId("tom_user_id");
        newUser.setFirstName("khanh");
        newUser.setLastName("le");
        newUser.setEmail("khanh@gmail.com");
        newUser.setEncryptedPassword("12345678");

        testEntityManager.persistAndFlush(newUser);

        //act & assert
        assertThrows(PersistenceException.class, () -> {
            testEntityManager.persistAndFlush(userEntity);
        }, "Expected PersistenceException to be thrown");
    }
}
