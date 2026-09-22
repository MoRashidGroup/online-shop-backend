package com.morashid.OnlineShop.config;

import com.morashid.OnlineShop.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * FileController - ina-handle file uploads.
 * 
 * Endpoints:
 *   POST /api/files/upload       → Upload picha MOJA
 *   POST /api/files/upload-multiple → Upload picha NYINGI
 */
@Slf4j
@RestController
@RequestMapping("/api/files")
public class FileController {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.upload.base-url:http://localhost:8383}")
    private String baseUrl;

    /**
     * Allowed extensions.
     */
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "webp"
    );

    /**
     * Allowed content types.
     */
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    /**
     * Upload picha MOJA.
     * 
     * POST /api/files/upload
     * Content-Type: multipart/form-data
     * Body: file=<file>
     * 
     * Response: { success, data: { url, filename, size } }
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file) {

        validateFile(file);

        String filename = saveFile(file);
        String url = baseUrl + "/uploads/" + filename;

        Map<String, Object> data = new HashMap<>();
        data.put("url", url);
        data.put("filename", filename);
        data.put("size", file.getSize());
        data.put("originalName", file.getOriginalFilename());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "File uploaded successfully");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    /**
     * Upload picha NYINGI kwa pamoja.
     * 
     * POST /api/files/upload-multiple
     * Content-Type: multipart/form-data
     * Body: files=<file1>&files=<file2>...
     * 
     * Response: { success, data: { urls: [...], count } }
     */
    @PostMapping("/upload-multiple")
    public ResponseEntity<Map<String, Object>> uploadMultipleFiles(
            @RequestParam("files") List<MultipartFile> files) {

        if (files == null || files.isEmpty()) {
            throw new BadRequestException("No files provided");
        }

        if (files.size() > 10) {
            throw new BadRequestException("Maximum 10 files per upload");
        }

        List<Map<String, Object>> uploadedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            validateFile(file);
            String filename = saveFile(file);
            String url = baseUrl + "/uploads/" + filename;

            Map<String, Object> fileData = new HashMap<>();
            fileData.put("url", url);
            fileData.put("filename", filename);
            fileData.put("size", file.getSize());
            fileData.put("originalName", file.getOriginalFilename());

            uploadedFiles.add(fileData);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", files.size() + " file(s) uploaded successfully");
        response.put("data", Map.of(
                "files", uploadedFiles,
                "count", uploadedFiles.size()
        ));

        return ResponseEntity.ok(response);
    }

    // ============================================
    // HELPERS
    // ============================================

    /**
     * Validate file (size, type, extension).
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        // Check content type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException(
                    "Invalid file type. Allowed: JPG, PNG, GIF, WEBP"
            );
        }

        // Check extension
        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            throw new BadRequestException("File name is missing");
        }

        String extension = getExtension(originalName).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException(
                    "Invalid file extension. Allowed: " + ALLOWED_EXTENSIONS
            );
        }

        // Size check (5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BadRequestException("File size exceeds 5MB");
        }
    }

    /**
     * Save file kwenye disk.
     * 
     * @return filename mpya (unique)
     */
    private String saveFile(MultipartFile file) {
        try {
            // Unda folder kama haipo
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            // Unda filename unique
            String originalName = file.getOriginalFilename();
            String extension = getExtension(originalName);
            String newFilename = UUID.randomUUID() + "." + extension;

            // Copy file
            Path targetLocation = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("File saved: {}", targetLocation);
            return newFilename;

        } catch (IOException ex) {
            log.error("Failed to save file", ex);
            throw new RuntimeException("Failed to save file: " + ex.getMessage());
        }
    }

    /**
     * Get extension kutoka filename.
     */
    private String getExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) {
            throw new BadRequestException("File has no extension");
        }
        return filename.substring(lastDot + 1);
    }

    
}