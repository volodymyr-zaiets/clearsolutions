package com.example;

import com.example.exception.ConflictException;
import com.example.model.User;
import com.example.payload.request.UserFindByBirthDateBetweenRequest;
import com.example.payload.request.UserUpdateRequest;
import com.example.payload.response.MessageResponse;
import com.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService.getUserMap().clear();
    }

    @Test
    void createUserSuccess() {
        User user = new User("John", "Doe", "john@example.com",
                LocalDate.of(1990, 1, 1), "123 Street", "1234567890");
        User createdUser = userService.createUser(user);
        assertNotNull(createdUser.getId());
        assertEquals(user.getFirstName(), createdUser.getFirstName());
        assertEquals(user.getLastName(), createdUser.getLastName());
        assertEquals(user.getEmail(), createdUser.getEmail());
        assertEquals(user.getBirthDate(), createdUser.getBirthDate());
        assertEquals(user.getAddress(), createdUser.getAddress());
        assertEquals(user.getPhoneNumber(), createdUser.getPhoneNumber());
    }

    @Test
    void createUserConflictException() {
        User user = new User("John", "Doe", "john@example.com",
                LocalDate.of(1990, 1, 1), "123 Street", "1234567890");
        userService.createUser(user);
        assertThrows(ConflictException.class, () -> userService.createUser(user));
    }

    @Test
    void getUserByIdSuccess() {
        User user = new User("John", "Doe", "john@example.com",
                LocalDate.of(1990, 1, 1), "123 Street", "1234567890");
        User createdUser = userService.createUser(user);
        User retrievedUser = userService.getUserById(createdUser.getId());
        assertEquals(createdUser, retrievedUser);
    }

    @Test
    void updateUserByIdSuccess() {
        User user = new User("John", "Doe", "john@example.com",
                LocalDate.of(1990, 1, 1), "123 Street", "1234567890");
        User createdUser = userService.createUser(user);

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setEmail("updated@example.com");
        updateRequest.setFirstName("Updated");
        updateRequest.setLastName("User");
        updateRequest.setBirthDate(LocalDate.of(1995, 1, 1));
        updateRequest.setAddress("456 Avenue");
        updateRequest.setPhoneNumber("0987654321");

        User updatedUser = userService.updateUserById(createdUser.getId(), updateRequest);
        assertEquals(updateRequest.getEmail(), updatedUser.getEmail());
        assertEquals(updateRequest.getFirstName(), updatedUser.getFirstName());
        assertEquals(updateRequest.getLastName(), updatedUser.getLastName());
        assertEquals(updateRequest.getBirthDate(), updatedUser.getBirthDate());
        assertEquals(updateRequest.getAddress(), updatedUser.getAddress());
        assertEquals(updateRequest.getPhoneNumber(), updatedUser.getPhoneNumber());
    }

    @Test
    void updateUserByIdConflictException() {
        User user1 = new User("John", "Doe", "john@example.com",
                LocalDate.of(1990, 1, 1), "123 Street", "1234567890");
        User createdUser1 = userService.createUser(user1);

        User user2 = new User("Jane", "Doe", "jane@example.com",
                LocalDate.of(1995, 1, 1), "456 Avenue", "0987654321");
        userService.createUser(user2);

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setEmail(user2.getEmail());
        assertThrows(ConflictException.class, () -> userService.updateUserById(createdUser1.getId(), updateRequest));
    }

    @Test
    void deleteUserByIdSuccess() {
        User user = new User("John", "Doe", "john@example.com",
                LocalDate.of(1990, 1, 1), "123 Street", "1234567890");
        User createdUser = userService.createUser(user);

        MessageResponse response = userService.deleteUserById(createdUser.getId());
        assertEquals("User deleted successfully", response.getMessage());
        assertFalse(userService.getUserMap().containsKey(createdUser.getId()));
    }

    @Test
    void deleteUserByIdNoSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> userService.deleteUserById(9999L));
    }

    @Test
    void findUsersByBirthDateBetweenSuccess() {
        User user1 = new User("John", "Doe", "john@example.com",
                LocalDate.of(1990, 1, 1), "123 Street", "1234567890");
        User user2 = new User("Jane", "Doe", "jane@example.com",
                LocalDate.of(1995, 1, 1), "456 Avenue", "0987654321");
        userService.createUser(user1);
        userService.createUser(user2);

        UserFindByBirthDateBetweenRequest request = new UserFindByBirthDateBetweenRequest();
        request.setFromDate(LocalDate.of(1990, 1, 1));
        request.setToDate(LocalDate.of(1995, 1, 1));

        List<User> users = userService.findUsersByBirthDateBetween(request);
        assertEquals(2, users.size());
    }

    @Test
    void findUsersByBirthDateBetweenInvalidDateRange() {
        UserFindByBirthDateBetweenRequest request = new UserFindByBirthDateBetweenRequest();
        request.setFromDate(LocalDate.of(1995, 1, 1));
        request.setToDate(LocalDate.of(1990, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> userService.findUsersByBirthDateBetween(request));
    }
}
