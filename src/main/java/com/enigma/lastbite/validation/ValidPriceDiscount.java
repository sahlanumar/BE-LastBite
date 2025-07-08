package com.enigma.lastbite.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPriceDiscountValidator.class)
public @interface ValidPriceDiscount {
    String message() default "Harga diskon tidak boleh lebih besar dari harga asli";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}