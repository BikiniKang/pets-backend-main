package com.example.pets_backend;

import com.example.pets_backend.entity.User;
import com.example.pets_backend.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class RegisterTestCase {

    private final String email = "unittest1@gmail.com";
    private final String password = "123456";
    private final String firstName = "unit";
    private final String lastName = "test1";

    @Resource
    private UserService userService;

    @BeforeEach
    public void beforeEach() {
        User user = userService.findByEmail(email);
        if (user != null) {
            userService.deleteByUid(user.getUid());
        }
    }

    @Test //correct case
    public void registerTest1() {
        User user = new User(email, password, firstName, lastName);
        User savedUser = userService.save(user);
        // should not directly compare the two User instances because there are pre-defined folders created in the register process
        Assertions.assertEquals(user.getEmail(), savedUser.getEmail());
        Assertions.assertEquals(user.getPassword(), savedUser.getPassword());
        Assertions.assertEquals(user.getFirstName(), savedUser.getFirstName());
        Assertions.assertEquals(user.getLastName(), savedUser.getLastName());
    }
}
