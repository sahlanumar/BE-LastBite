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
            return true; // Biarkan validasi lain (seperti @NotNull) yang menanganinya
        }

        // Menggunakan StringUtils.hasText untuk memeriksa apakah string tidak null,
        // tidak kosong, dan tidak hanya berisi spasi.
        boolean isUsernameProvided = StringUtils.hasText(loginRequest.getUsername());
        boolean isEmailProvided = StringUtils.hasText(loginRequest.getEmail());

        // Validasi berhasil jika salah satu dari keduanya (atau keduanya) diisi
        return isUsernameProvided || isEmailProvided;
    }
}