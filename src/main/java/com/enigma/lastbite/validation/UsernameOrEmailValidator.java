package com.enigma.lastbite.validation;

import com.enigma.lastbite.dto.request.LoginRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

/**
 * Kelas ini berisi logika validasi untuk anotasi @UsernameOrEmailRequired.
 * Ia akan memeriksa objek LoginRequest dan memastikan setidaknya salah satu
 * dari username atau email memiliki teks yang valid.
 */
public class UsernameOrEmailValidator implements ConstraintValidator<UsernameOrEmailRequired, LoginRequest> {

    @Override
    public boolean isValid(LoginRequest loginRequest, ConstraintValidatorContext context) {
        if (loginRequest == null) {
            return true;
        }

        boolean isUsernameProvided = StringUtils.hasText(loginRequest.getUsername());
        boolean isEmailProvided = StringUtils.hasText(loginRequest.getEmail());

        return isUsernameProvided || isEmailProvided;
    }
}