package com.voicevault.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Service
public class SupabaseService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service-key}")
    private String serviceKey;

    @Value("${supabase.bucket}")
    private String bucket;

    private final WebClient webClient = WebClient.builder().build();

    public String uploadRecording(MultipartFile file, String userId) throws Exception {
        String fileName = userId + "/" + UUID.randomUUID() + ".webm";
        String url = supabaseUrl + "/storage/v1/object/" + bucket + "/" + fileName;

        byte[] bytes = file.getBytes();

        webClient.post()
                .uri(url)
                .header("apikey", serviceKey)
                .header("Authorization", "Bearer " + serviceKey)
                .header("Content-Type", "audio/webm")
                .bodyValue(bytes)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return fileName;
    }

    public String getPublicUrl(String fileName) {
        return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + fileName;
    }

    public void deleteRecording(String userId, String fileName) {
        String path = userId + "/" + fileName;
        String url = supabaseUrl + "/storage/v1/object/" + bucket + "/" + path;

        webClient.delete()
                .uri(url)
                .header("apikey", serviceKey)
                .header("Authorization", "Bearer " + serviceKey)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
