package org.tommap.io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@TestPropertySource(locations = "/application-test.properties")
class UsersRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UsersRepository usersRepository;

    @BeforeEach
    void init() {
        UserEntity firstUser = new UserEntity();
        firstUser.setUserId("tom_user_id");
        firstUser.setFirstName("tom");
        firstUser.setLastName("map");
        firstUser.setEmail("tom@gmail.com");
        firstUser.setEncryptedPassword("12345678");
        testEntityManager.persistAndFlush(firstUser);

        UserEntity secondUser = new UserEntity();
        secondUser.setUserId("pun_user_id");
        secondUser.setFirstName("pun");
        secondUser.setLastName("du");
        secondUser.setEmail("pundu@gmail.com");
        secondUser.setEncryptedPassword("12345678");
        testEntityManager.persistAndFlush(secondUser);

        UserEntity thirdUser = new UserEntity();
        thirdUser.setUserId("jerry_user_id");
        thirdUser.setFirstName("jerry");
        thirdUser.setLastName("map");
        thirdUser.setEmail("jerry@test.com");
        thirdUser.setEncryptedPassword("12345678");
        testEntityManager.persistAndFlush(thirdUser);
    }

    @Test
    @DisplayName("find by correct email")
    void testFindByEmail_WhenCorrectEmailProvided_ShouldReturnUserDetails() {
        //arrange

        //act
        UserEntity user = usersRepository.findByEmail("tom@gmail.com");

        //assert
        assertNotNull(user, "UserEntity is null");
        assertEquals("tom", user.getFirstName(), "FirstName should be tom");
        assertEquals("map", user.getLastName(), "LastName should be map");
        assertEquals("tom@gmail.com", user.getEmail(), "Email should be tom@gmail.com");
    }

    @Test
    @DisplayName("find by incorrect email")
    void testFindByEmail_WhenIncorrectEmailProvided_ShouldReturnNull() {
        //arrange

        //act
        UserEntity user = usersRepository.findByEmail("test@gmail.com");

        //assert
        assertNull(user, "UserEntity should be null");
    }

    @Test
    @DisplayName("find by correct user_id")
    void testFindByUserId_WhenCorrectUserIdProvided_ShouldReturnUserDetails() {
        //arrange

        //act
        UserEntity user = usersRepository.findByUserId("pun_user_id");

        //assert
        assertNotNull(user, "UserEntity is null");
        assertEquals("pun", user.getFirstName(), "FirstName should be pun");
        assertEquals("du", user.getLastName(), "LastName should be du");
        assertEquals("pundu@gmail.com", user.getEmail(), "Email should be pundu@gmail.com");
    }

    @Test
    @DisplayName("find by correct domain email")
    void testFindUsersWithEmailEndingWith_WhenCorrectEmailDomainProvided_ShouldReturnListUsers() {
        //arrange

        //act
        List<UserEntity> users = usersRepository.findUsersWithEmailEndingWith("gmail.com");

        //assert
        assertNotNull(users, "users should not be null");
        assertEquals(2, users.size(), "users size should be 2");
        assertTrue(users.get(0).getEmail().contains("@gmail.com"), "Email should contain @gmail.com");
    }
}
