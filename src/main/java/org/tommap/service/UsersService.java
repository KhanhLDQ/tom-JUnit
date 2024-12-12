package org.tommap.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.tommap.shared.UserDto;

import java.util.List;

public interface UsersService extends UserDetailsService {
    UserDto createUser(UserDto user);
    List<UserDto> getUsers(int page, int limit);
    UserDto getUser(String email);
}
