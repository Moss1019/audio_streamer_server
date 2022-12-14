package com.mossonthetree.audioserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/health")
public class HealthController {
    @GetMapping("")
    public ResponseEntity<?> getStatus() {
        return ResponseEntity.ok("{\"status\":\"healthy\"}");
    }
}
