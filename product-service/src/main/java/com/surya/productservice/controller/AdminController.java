package com.surya.productservice.controller;

import com.surya.productservice.security.AdminPasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminPasswordService adminPasswordService;

    public AdminController(AdminPasswordService adminPasswordService) {
        this.adminPasswordService = adminPasswordService;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody AdminLoginRequest request) {
        if (!adminPasswordService.isValid(request.password())) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.noContent().build();
    }

    public record AdminLoginRequest(String password) {
    }
}
