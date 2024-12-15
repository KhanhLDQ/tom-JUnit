package org.tommap.ui.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.tommap.service.UsersService;
import org.tommap.shared.UserDto;
import org.tommap.ui.request.UserDetailsRequestModel;
import org.tommap.ui.response.UserRest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/*
    - MockMvc
        + do not need to start embedded servlet container (Tomcat | Jetty | ...) => not occupy any port
        + use MockMvc instance to interact with mocked environment and do not initiate real HTTP requests

    - @MockBean
        + create mock object and automatically load it into Spring application context
    - @Mock
        + only create mock object but not load it into Spring application context
 */
@WebMvcTest( //only loads the beans related to the web layer into Spring application context
        controllers = UsersController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class} //exclude security layer
)
class UsersControllerWebLayerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsersService usersService; //create mock object for the implementation of UsersService interface

    UserDetailsRequestModel requestModel;
    UserDto userDto;

    @BeforeEach
    void init() {
        requestModel = new UserDetailsRequestModel();
        requestModel.setFirstName("Tom");
        requestModel.setLastName("Map");
        requestModel.setEmail("tom@gmail.com");
        requestModel.setPassword("12345678");
        requestModel.setRepeatPassword("12345678");

        userDto = new ModelMapper().map(requestModel, UserDto.class);
        userDto.setUserId(UUID.randomUUID().toString());
    }

    //naming convention - test<MethodUnderTest>_<Scenario>_<ExpectedBehavior>
    @Test
    @DisplayName("Create user success")
    void testCreateUser_WhenValidRequestDataProvided_ShouldReturnCreatedUserDetails() throws Exception {
        //arrange
        int expectedStatus = HttpStatus.OK.value();

        when(usersService.createUser(any(UserDto.class))).thenReturn(userDto);

        RequestBuilder requestBuilder = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestModel));

        //act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        UserRest response = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), UserRest.class);
        int actualStatus = mvcResult.getResponse().getStatus();

        //assert
        assertEquals(expectedStatus, actualStatus, "Status should be 200");
        assertNotNull(response.getUserId(), "UserID should not be null");
        assertEquals("Tom", response.getFirstName(), "First name should be Tom");
        assertEquals("Map", response.getLastName(), "Last name should be Map");
        assertEquals("tom@gmail.com", response.getEmail(), "Email should be tom@gmail.com");
    }

    @ParameterizedTest(name = "[{index}] - {1}")
    @MethodSource("invalidFirstNameCases")
    @DisplayName("Invalid first name provided")
    void testCreateUser_WhenInvalidFirstNameProvided_ShouldThrowBadRequestException(
            String firstName, String description
    ) throws Exception {
        //arrange
        int expectedStatus = HttpStatus.BAD_REQUEST.value();

        requestModel.setFirstName(firstName);

        RequestBuilder requestBuilder = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestModel));

        //act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        int actualStatus = mvcResult.getResponse().getStatus();

        //assert
        assertEquals(expectedStatus, actualStatus, "Status should be 400");
    }

    private static Stream<Arguments> invalidFirstNameCases() {
        return Stream.of(
                Arguments.of("", "First name is empty"),
                Arguments.of("T", "First name is less than 2 characters")
        );
    }

    @ParameterizedTest(name = "[{index}] - {1}")
    @MethodSource("invalidLastNameCases")
    @DisplayName("Invalid last name provided")
    void testCreateUser_WhenInvalidLastNameProvided_ShouldThrowBadRequestException(
            String lastName, String description
    ) throws Exception {
        //arrange
        int expectedStatus = HttpStatus.BAD_REQUEST.value();

        requestModel.setLastName(lastName);

        RequestBuilder requestBuilder = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestModel));

        //act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        int actualStatus = mvcResult.getResponse().getStatus();

        //assert
        assertEquals(expectedStatus, actualStatus, "Status should be 400");
    }

    private static Stream<Arguments> invalidLastNameCases() {
        return Stream.of(
                Arguments.of("", "Last name is empty"),
                Arguments.of("M", "Last name is less than 2 characters")
        );
    }

    @Test
    @DisplayName("Invalid email provided")
    void testCreateUser_WhenInvalidEmailProvided_ShouldThrowBadRequestException() throws Exception {
        //arrange
        int expectedStatus = HttpStatus.BAD_REQUEST.value();

        requestModel.setEmail("tom@");

        RequestBuilder requestBuilder = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestModel));

        //act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        int actualStatus = mvcResult.getResponse().getStatus();

        //assert
        assertEquals(expectedStatus, actualStatus, "Status should be 400");
    }

    @ParameterizedTest(name = "[{index}] - {1}")
    @MethodSource("invalidPasswordCases")
    @DisplayName("Invalid password provided")
    void testCreateUser_WhenInvalidPasswordProvided_ShouldThrowBadRequestException(
            String password, String description
    ) throws Exception {
        //arrange
        int expectedStatus = HttpStatus.BAD_REQUEST.value();

        requestModel.setPassword(password);

        RequestBuilder requestBuilder = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestModel));

        //act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        int actualStatus = mvcResult.getResponse().getStatus();

        //assert
        assertEquals(expectedStatus, actualStatus, "Status should be 400");

    }

    private static Stream<Arguments> invalidPasswordCases() {
        return Stream.of(
                Arguments.of("123456", "Password is less than 8 characters"),
                Arguments.of("12345678901234567", "Password is more than 16 characters")
        );
    }

    @Test
    @DisplayName("Default page and limit")
    void testGetUsers_WhenNoParamsProvided_ShouldUseDefaultPageAndLimit() throws Exception {
        //arrange
        int expectedStatus = HttpStatus.OK.value();

        when(usersService.getUsers(0, 2)).thenReturn(List.of(userDto));

        RequestBuilder requestBuilder = get("/users")
                .accept(MediaType.APPLICATION_JSON)
                .param("page", "0")
                .param("limit", "2");

        //act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        List<UserRest> response = new ObjectMapper().readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<UserRest>>() {}
        );
        int actualStatus = mvcResult.getResponse().getStatus();

        //assert
        assertEquals(expectedStatus, actualStatus, "Status should be 200");
        assertFalse(response.isEmpty(), "Response should not be empty");

        UserRest userRest = response.get(0);
        assertNotNull(userRest.getUserId(), "UserID should not be null");
        assertEquals("Tom", userRest.getFirstName(), "First name should be Tom");
        assertEquals("Map", userRest.getLastName(), "Last name should be Map");
        assertEquals("tom@gmail.com", userRest.getEmail(), "Email should be tom@gmail.com");
    }

    @Test
    @DisplayName("Invalid page provided")
    void testGetUsers_WhenInvalidPageProvided_ShouldThrowBadRequestException() throws Exception {
        //arrange
        int expectedStatus = HttpStatus.BAD_REQUEST.value();
        String expectedExceptionMessage = "Page must be greater than or equal to 0";

        when(usersService.getUsers(-1, 2)).thenThrow(new IllegalArgumentException("Page must be greater than or equal to 0"));

        RequestBuilder requestBuilder = get("/users")
                .accept(MediaType.APPLICATION_JSON)
                .param("page", "-1")
                .param("limit", "2");

        //act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        int actualStatus = mvcResult.getResponse().getStatus();
        String actualExceptionMessage = mvcResult.getResponse().getContentAsString();

        //assert
        assertEquals(expectedStatus, actualStatus, "Status should be 400");
        assertEquals(expectedExceptionMessage, actualExceptionMessage, "Exception message should be 'Page must be greater than or equal to 0'");
    }
}
