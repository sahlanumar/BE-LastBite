package com.enigma.lastbite.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotasi validasi untuk memastikan dua field password cocok.
 * Anotasi ini akan diterapkan pada level kelas.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordsMatchValidator.class)
public @interface PasswordsMatch {
    String message() default "Konfirmasi password baru tidak cocok dengan password baru";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}