package com.enigma.lastbite.validation;

import com.enigma.lastbite.dto.request.PasswordChangeRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Kelas ini berisi logika untuk anotasi @PasswordsMatch.
 * Memeriksa apakah newPassword dan confirmNewPassword pada objek sama.
 */
public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, PasswordChangeRequest> {

    @Override
    public boolean isValid(PasswordChangeRequest request, ConstraintValidatorContext context) {
        // Jika request null atau salah satu password null, biarkan anotasi @NotBlank yang menanganinya
        if (request == null || request.getNewPassword() == null || request.getConfirmNewPassword() == null) {
            return true;
        }
        // Validasi berhasil jika kedua password cocok
        return request.getNewPassword().equals(request.getConfirmNewPassword());
    }
}