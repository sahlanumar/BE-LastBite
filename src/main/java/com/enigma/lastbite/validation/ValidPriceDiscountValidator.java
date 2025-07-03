package com.enigma.lastbite.validation;

import com.enigma.lastbite.dto.request.MenuItemCreateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class ValidPriceDiscountValidator implements ConstraintValidator<ValidPriceDiscount, MenuItemCreateRequest> {

    @Override
    public boolean isValid(MenuItemCreateRequest request, ConstraintValidatorContext context) {
        // Jika salah satu harga null, kita anggap valid karena validasi @NotNull akan menanganinya
        if (request.getOriginalPrice() == null || request.getDiscountedPrice() == null) {
            return true;
        }

        // Cek jika harga diskon lebih besar dari harga asli
        // request.getDiscountedPrice().compareTo(request.getOriginalPrice()) > 0
        // artinya discountedPrice > originalPrice
        if (request.getDiscountedPrice().compareTo(request.getOriginalPrice()) > 0) {
            return false; // Tidak valid
        }

        return true; // Valid
    }
}