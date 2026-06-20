package com.recruitease.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload.dir:uploads/resumes}")
    private String uploadDir;

    public String storeResume(MultipartFile file, String studentEmail) throws IOException {
        if (file == null || file.isEmpty()) return null;

        String original = file.getOriginalFilename();
        String ext = (original != null && original.contains("."))
                ? original.substring(original.lastIndexOf("."))
                : ".pdf";

        String filename = studentEmail.replaceAll("[^a-zA-Z0-9]", "_")
                + "_" + UUID.randomUUID() + ext;

        Path dir = Paths.get(uploadDir);
        Files.createDirectories(dir);
        Files.copy(file.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        return filename;
    }

    public void deleteFile(String filename) {
        try {
            if (filename != null)
                Files.deleteIfExists(Paths.get(uploadDir).resolve(filename));
        } catch (IOException ignored) {}
    }

    public String getUploadDir() { return uploadDir; }
}
