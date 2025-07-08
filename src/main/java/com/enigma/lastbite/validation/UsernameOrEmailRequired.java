package com.enigma.lastbite.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotasi validasi untuk memastikan bahwa salah satu dari field 'username' atau 'email' diisi.
 * Anotasi ini akan diterapkan pada level kelas.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UsernameOrEmailValidator.class)
public @interface UsernameOrEmailRequired {
    String message() default "Username atau email harus diisi";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}