package com.examly.springapp.controller;

import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;


@RestController
@RequestMapping(path="/api/image")
public class ImageController {
    
    @GetMapping("/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
        try {

            if (!imageName.matches("[A-Za-z0-9_-]+\\.(jpg|jpeg|png)")) return ResponseEntity.badRequest().build();
            Path root = Paths.get("src/main/java/com/examly/springapp/test_imgs").toAbsolutePath().normalize();
            Path imagePath = root.resolve(imageName).normalize();
            if (!imagePath.startsWith(root)) return ResponseEntity.badRequest().build();
            Resource resource = new UrlResource(imagePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // Change based on image type
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
