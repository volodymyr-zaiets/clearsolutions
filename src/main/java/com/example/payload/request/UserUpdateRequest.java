package com.example.payload.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserUpdateRequest {
    @Size(max = 50)
    @Email
    private String email;

    @Size(max = 20)
    private String firstName;

    @Size(max = 20)
    private String lastName;

    @Past
    private LocalDate birthDate;

    @Size(max = 255)
    private String address;

    @Size(max = 16)
    private String phoneNumber;
}
