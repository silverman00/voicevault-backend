package com.voicevault.controller;

import com.voicevault.model.Recording;
import com.voicevault.model.RecordingEntity;
import com.voicevault.repository.RecordingRepository;
import com.voicevault.service.SupabaseService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recordings")
public class RecordingController {

    @Autowired
    private SupabaseService supabaseService;

    @Autowired
    private RecordingRepository recordingRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
                                    @RequestParam(value = "duration", defaultValue = "0") long duration,
                                    HttpServletRequest request) {
        try {
            String userId = (String) request.getAttribute("userId");
            if (userId == null) return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));

            String fileName = supabaseService.uploadRecording(file, userId);
            String publicUrl = supabaseService.getPublicUrl(fileName);

            RecordingEntity entity = new RecordingEntity();
            entity.setUserId(userId);
            entity.setFileName(fileName.substring(fileName.lastIndexOf("/") + 1));
            entity.setPublicUrl(publicUrl);
            entity.setDuration(duration);
            entity.setFileSize(file.getSize());
            recordingRepository.save(entity);

            Recording rec = toDto(entity);
            return ResponseEntity.ok(rec);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> list(HttpServletRequest request) {
        try {
            String userId = (String) request.getAttribute("userId");
            if (userId == null) return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));

            List<Recording> recordings = recordingRepository
                    .findByUserIdOrderByCreatedAtDesc(userId)
                    .stream().map(this::toDto).collect(Collectors.toList());

            return ResponseEntity.ok(recordings);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id, HttpServletRequest request) {
        try {
            String userId = (String) request.getAttribute("userId");
            if (userId == null) return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));

            RecordingEntity entity = recordingRepository.findById(id).orElse(null);
            if (entity == null || !entity.getUserId().equals(userId)) {
                return ResponseEntity.status(404).body(Map.of("message", "Not found"));
            }

            supabaseService.deleteRecording(userId, entity.getFileName());
            recordingRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }

    private Recording toDto(RecordingEntity e) {
        Recording r = new Recording();
        r.setId(e.getId());
        r.setUserId(e.getUserId());
        r.setFileName(e.getFileName());
        r.setPublicUrl(e.getPublicUrl());
        r.setDuration(e.getDuration());
        r.setFileSize(e.getFileSize());
        r.setCreatedAt(e.getCreatedAt() != null ? e.getCreatedAt().toString() : "");
        return r;
    }
}
