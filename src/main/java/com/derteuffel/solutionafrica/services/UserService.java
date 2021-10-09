package com.derteuffel.solutionafrica.services;

import com.derteuffel.solutionafrica.entities.User;
import com.derteuffel.solutionafrica.helpers.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    User findByUsernameOrEmail(String username, String email);
    User save(UserDto userDto);
    User update(User user);
    List<User> findAll();
    User getOne(Long id);
}
