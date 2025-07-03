package com.enigma.lastbite.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE}) // Anotasi ini akan digunakan di level kelas
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPriceDiscountValidator.class) // Tentukan kelas validatornya
public @interface ValidPriceDiscount {
    String message() default "Harga diskon tidak boleh lebih besar dari harga asli";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}