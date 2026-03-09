package com.voicevault.controller;

import com.voicevault.service.LiveKitService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/calls")
public class CallController {

    @Autowired
    private LiveKitService liveKitService;

    @PostMapping("/token")
    public ResponseEntity<?> getToken(@RequestBody Map<String, String> body,
                                       HttpServletRequest request) {
        try {
            String userId = (String) request.getAttribute("userId");
            if (userId == null) return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));

            String roomName = body.getOrDefault("roomName", "room-" + userId);
            String token = liveKitService.generateToken(roomName, userId);

            return ResponseEntity.ok(Map.of(
                "token", token,
                "roomName", roomName,
                "wsUrl", liveKitService.getWsUrl()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }
}
