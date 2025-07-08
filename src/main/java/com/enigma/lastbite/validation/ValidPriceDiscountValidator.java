package com.enigma.lastbite.validation;

import com.enigma.lastbite.dto.request.MenuItemCreateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class ValidPriceDiscountValidator implements ConstraintValidator<ValidPriceDiscount, MenuItemCreateRequest> {

    @Override
    public boolean isValid(MenuItemCreateRequest request, ConstraintValidatorContext context) {
        if (request.getOriginalPrice() == null || request.getDiscountedPrice() == null) {
            return true;
        }
        if (request.getDiscountedPrice().compareTo(request.getOriginalPrice()) > 0) {
            return false;
        }

        return true;
    }
}