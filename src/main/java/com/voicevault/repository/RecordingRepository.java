package com.voicevault.repository;

import com.voicevault.model.RecordingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecordingRepository extends JpaRepository<RecordingEntity, String> {
    List<RecordingEntity> findByUserIdOrderByCreatedAtDesc(String userId);
    void deleteByIdAndUserId(String id, String userId);
}
