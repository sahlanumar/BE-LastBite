package com.enigma.lastbite.service;

import com.enigma.lastbite.dto.response.UploadImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    UploadImageResponse uploadFile(MultipartFile file) throws IOException;
    Map deleteFile(String publicId) throws IOException;
}
