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
@Target({ElementType.TYPE}) // Menandakan anotasi ini untuk digunakan pada kelas
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UsernameOrEmailValidator.class) // Menunjuk ke kelas yang berisi logika validasi
public @interface UsernameOrEmailRequired {
    // Pesan error default jika validasi gagal
    String message() default "Username atau email harus diisi";

    // Boilerplate yang dibutuhkan untuk custom validation
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}