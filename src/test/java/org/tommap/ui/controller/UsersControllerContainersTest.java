package org.tommap.ui.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.tommap.ui.response.UserRest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/application-test.properties")
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UsersControllerContainersTest {
    @Container //automatically start before running any tests and stop-delete this container after all tests are executed
    @ServiceConnection //automatically override properties to MySQLContainer
    private static MySQLContainer mySQLContainer = //use static to share the same container instance across all test methods
            new MySQLContainer("mysql:8.4.0")
                    .withDatabaseName("mysql_testing_database")
                    .withUsername("tom")
                    .withPassword("123456");

    static {
        mySQLContainer.start();
    }

    @DynamicPropertySource
    private static void overrideProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
//        registry.add("spring.datasource.username", mySQLContainer::getUsername);
//        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", mySQLContainer::getDriverClassName);
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.MySQL8Dialect");
    }

    @Autowired
    private TestRestTemplate testRestTemplate;

    String jwt;

    @Test
    @DisplayName("MySQL container is created and running")
    @Order(1)
    void testMySQLContainerIsRunning() {
        //assert
        assertTrue(mySQLContainer.isCreated(), "MySQL container has not been created");
        assertTrue(mySQLContainer.isRunning(), "MySQL container is not running");
    }

    @Test
    @DisplayName("Create user success")
    @Order(2)
    void testCreateUser_WhenValidRequestDataProvided_ShouldReturnCreatedUserDetails() {
        //arrange
        int expectedStatusCode = HttpStatus.OK.value();

        JSONObject requestModel = new JSONObject(Map.of(
                "firstName", "pun",
                "lastName", "du",
                "email", "pundu@gmail.com",
                "password", "12345678",
                "repeatPassword", "12345678"
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(requestModel.toString(), headers);

        //act
        ResponseEntity<UserRest> response = testRestTemplate.postForEntity("/users", request, UserRest.class);
        int actualStatusCode = response.getStatusCode().value();
        UserRest createdUserDetails = response.getBody();

        //assert
        assertEquals(expectedStatusCode, actualStatusCode, "Status code should be 200");
        assertNotNull(createdUserDetails, "User details should not be null");
        assertNotNull(createdUserDetails.getUserId(), "User Id should not be null");
        assertEquals("pun", createdUserDetails.getFirstName(), "First name should be pun");
        assertEquals("du", createdUserDetails.getLastName(), "Last name should be du");
        assertEquals("pundu@gmail.com", createdUserDetails.getEmail(), "Email should be pundu@gmail.com");
    }

    @Test
    @DisplayName("Get users requires JWT")
    @Order(3)
    void testGetUsers_WhenMissingJWT_ShouldThrowForbidden() {
        //arrange
        int expectedStatusCode = HttpStatus.FORBIDDEN.value();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity request = new HttpEntity(headers);

        //act
        ResponseEntity<List<UserRest>> response =
                testRestTemplate.exchange(
                        "/users",
                        HttpMethod.GET,
                        request,
                        new ParameterizedTypeReference<List<UserRest>>() {}
                );
        int actualStatusCode = response.getStatusCode().value();

        //assert
        assertEquals(expectedStatusCode, actualStatusCode, "Status code should be 403");
    }

    @Test
    @DisplayName("User login success")
    @Order(4)
    void testUserLogin_WhenValidCredentialsProvided_ShouldReturnJWT() {
        //arrange
        int expectedStatusCode = HttpStatus.OK.value();

        JSONObject requestModel = new JSONObject(Map.of(
                "email", "pundu@gmail.com",
                "password", "12345678"
        ));

        HttpEntity<String> request = new HttpEntity<>(requestModel.toString());

        //act
        ResponseEntity<Object> response = testRestTemplate.postForEntity("/users/login", request, null); //uri - request - responseType
        int actualStatusCode = response.getStatusCode().value();
        jwt = response.getHeaders().getValuesAsList("Authorization").get(0);

        //assert
        assertEquals(expectedStatusCode, actualStatusCode, "Status code should be 200");
        assertNotNull(jwt, "Authorization header should not be null");
        assertNotNull(response.getHeaders().getValuesAsList("UserID").get(0), "UserID header should not be null");
    }

    @Test
    @DisplayName("Get users success")
    @Order(5)
    void testGetUsers_WhenJWTProvided_ShouldReturnUsers() {
        //arrange
        int expectedStatusCode = HttpStatus.OK.value();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Authorization", jwt);

        HttpEntity request = new HttpEntity(headers);

        //act
        ResponseEntity<List<UserRest>> response =
                testRestTemplate.exchange(
                        "/users",
                        HttpMethod.GET,
                        request,
                        new ParameterizedTypeReference<List<UserRest>>() {}
                );
        int actualStatusCode = response.getStatusCode().value();
        List<UserRest> users = response.getBody();

        //assert
        assertEquals(expectedStatusCode, actualStatusCode, "Status code should be 200");
        assertNotNull(users, "Users list should not be null");
        assertFalse(users.isEmpty(), "Users list should not be empty");
    }
}
