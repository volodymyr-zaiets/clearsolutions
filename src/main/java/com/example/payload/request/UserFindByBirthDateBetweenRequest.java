package com.example.payload.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserFindByBirthDateBetweenRequest {
    @NotNull
    @Past
    LocalDate fromDate;

    @NotNull
    @Past
    LocalDate toDate;
}
