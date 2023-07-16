package com.decathlon.users.dto;

import com.decathlon.users.enumerations.UserDepartment;
import com.decathlon.users.enumerations.UserRole;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditUserDto {

    @Nullable
    private String firstName;

    @Nullable
    private String lastName;

    @Nullable
    @Pattern(regexp = "[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@um5.ac.ma$", message = "The email is invalid")
    private String email;

    @Nullable
    private UserRole role;

    @Nullable
    private UserDepartment department;
}
