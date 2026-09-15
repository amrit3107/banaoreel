package com.banaoreel.backend.repository;

import com.banaoreel.backend.entity.VideoJob;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface VideoJobRepository extends JpaRepository<VideoJob, UUID> {
    List<VideoJob> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
