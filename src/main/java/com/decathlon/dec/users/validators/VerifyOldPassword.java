package com.decathlon.dec.users.validators;

import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import jakarta.validation.Constraint;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = VerifyOldPasswordValidator.class)
public @interface VerifyOldPassword {
    
    String message() default "Incorrect current password";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
