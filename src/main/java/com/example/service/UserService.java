package com.example.service;

import com.example.exception.ConflictException;
import com.example.model.User;
import com.example.payload.request.UserFindByBirthDateBetweenRequest;
import com.example.payload.request.UserUpdateRequest;
import com.example.payload.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {
    private final ConcurrentHashMap<Long, User> userMap = new ConcurrentHashMap<>();

    private final AtomicLong userIdCounter = new AtomicLong(0);

    @Value("${app.validation.age.min}")
    private int minAge;

    public ConcurrentHashMap<Long, User> getUserMap() {
        return this.userMap;
    }

    public User createUser(User user) {
        if (validateBirthDate(user.getBirthDate())) {
            long userId = userIdCounter.incrementAndGet();
            user.setId(userId);
            if (!userMap.containsKey(userId)
                    && userMap.values().stream().noneMatch(u -> u.getEmail().equals(user.getEmail()))) {
                userMap.put(userId, user);
                return user;
            } else {
                throw new ConflictException("User with this email already exists");
            }
        }

        throw new IllegalArgumentException("User is not old enough");
    }

    public User getUserById(Long id) {
        User user = userMap.get(id);
        if (user != null) {
            return user;
        }

        throw new NoSuchElementException("User with this ID does not exist");
    }

    public User updateUserById(Long id, UserUpdateRequest userUpdateRequest) {
        User user = userMap.get(id);
        if (user != null) {
            if (userUpdateRequest.getEmail() != null && !user.getEmail().equals(userUpdateRequest.getEmail())
                    && userMap.values().stream().anyMatch(u -> u.getEmail().equals(userUpdateRequest.getEmail()))) {
                throw new ConflictException("User with this email already exists");
            }

            if (userUpdateRequest.getEmail() != null) {
                user.setEmail(userUpdateRequest.getEmail());
            }

            if (userUpdateRequest.getFirstName() != null) {
                user.setFirstName(userUpdateRequest.getFirstName());
            }

            if (userUpdateRequest.getLastName() != null) {
                user.setLastName(userUpdateRequest.getLastName());
            }

            if (userUpdateRequest.getBirthDate() != null) {
                if (!validateBirthDate(userUpdateRequest.getBirthDate())) {
                    throw new IllegalArgumentException("User is not old enough");
                }
                user.setBirthDate(userUpdateRequest.getBirthDate());
            }

            if (userUpdateRequest.getAddress() != null) {
                user.setAddress(userUpdateRequest.getAddress());
            }

            if (userUpdateRequest.getPhoneNumber() != null) {
                user.setPhoneNumber(userUpdateRequest.getPhoneNumber());
            }

            userMap.put(id, user);
            return user;
        }

        throw new NoSuchElementException("User with this ID does not exist");
    }

    public MessageResponse deleteUserById(Long id) {
        User user = userMap.remove(id);
        if (user != null) {
            return new MessageResponse("User deleted successfully");
        } else {
            throw new NoSuchElementException("User with this ID does not exist");
        }
    }

    public List<User> findUsersByBirthDateBetween(UserFindByBirthDateBetweenRequest userFindByBirthDateBetweenRequest) {
        LocalDate fromDate = userFindByBirthDateBetweenRequest.getFromDate();
        LocalDate toDate = userFindByBirthDateBetweenRequest.getToDate();

        if (!fromDate.isAfter(toDate)) {
            return userMap.values().stream()
                    .filter(user -> user.getBirthDate().isAfter(fromDate.minusDays(1))
                            && user.getBirthDate().isBefore(toDate.plusDays(1)))
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("Invalid date range");
        }
    }

    private boolean validateBirthDate(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears() >= minAge;
    }
}
