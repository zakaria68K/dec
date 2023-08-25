package com.decathlon.dec.users.dto;

import com.decathlon.dec.users.enumerations.UserDepartment;
import com.decathlon.dec.users.enumerations.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDto {
    @NotBlank(message = "The first name is required")
    private String firstName;

    @NotBlank(message = "The last name is required")
    private String lastName;

    @NotBlank(message = "The email is required")
    @Pattern(regexp = "[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@decathlon.com$", message = "The email is invalid")
    private String email;

    @NotBlank(message = "The password is required")
    @Pattern(regexp = "(?=^.{8,100}$)(?=.*\\d)(?=.*[^A-Za-z0-9]+)(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$", message = "The password must contain at least 1 uppercase, 1 lowercase, 1 special character and 1 digit and must be at least 8 characters")
    private String password;

    @NotBlank(message = "The password confirmation is required")
    @JsonProperty("confirmation")
    private String confirmPassowrd;

    @Nullable
    private UserRole role;

    @Nullable
    private long total;

    @Nullable
    private UserDepartment department;

    
}
