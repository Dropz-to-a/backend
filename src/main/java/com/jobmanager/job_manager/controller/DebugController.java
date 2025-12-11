package com.jobmanager.job_manager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class DebugController {
    private final TossProperties toss;
    public DebugController(TossProperties toss) { this.toss = toss; }

    @GetMapping("/api/debug/toss-key")
    public Map<String,String> check() {
        String key = toss.getSecretKey();
        return Map.of("loadedKey", key == null ? "❌ null" : key.substring(0, 10) + "****");
    }
}
