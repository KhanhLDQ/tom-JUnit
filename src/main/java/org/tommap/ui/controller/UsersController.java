package org.tommap.ui.controller;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tommap.service.UsersService;
import org.tommap.shared.UserDto;
import org.tommap.ui.request.UserDetailsRequestModel;
import org.tommap.ui.response.UserRest;

import java.lang.reflect.Type;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {
    UsersService usersService;

    @Autowired
    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping
    public UserRest createUser(@RequestBody @Valid UserDetailsRequestModel userDetails) throws Exception {
        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = new ModelMapper().map(userDetails, UserDto.class);

        UserDto createdUser = usersService.createUser(userDto);

        return modelMapper.map(createdUser, UserRest.class);
    }

    @GetMapping
    public List<UserRest> getUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "2") int limit
    ) {
        List<UserDto> users = usersService.getUsers(page, limit);

        Type listType = new TypeToken<List<UserRest>>() {}.getType();

        return new ModelMapper().map(users, listType);
    }
}
