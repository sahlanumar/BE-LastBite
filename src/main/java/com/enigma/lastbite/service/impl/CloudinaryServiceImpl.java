package com.enigma.lastbite.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.enigma.lastbite.dto.response.UploadImageResponse;
import com.enigma.lastbite.exception.CustomException;
import com.enigma.lastbite.exception.ErrorCode;
import com.enigma.lastbite.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {
    @Autowired
    private Cloudinary cloudinary;

    @Override
    public UploadImageResponse uploadFile(MultipartFile file) throws IOException {
        final long MAX_FILE_SIZE = 10 * 1024 * 1024;

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new CustomException(ErrorCode.FILE_TOO_LARGE);
        }

        Map result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return new UploadImageResponse(
                result.get("url").toString(),
                result.get("public_id").toString()
        );
    }

    public Map deleteFile(String publicId) throws IOException {
        return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}