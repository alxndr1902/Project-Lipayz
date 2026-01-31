package com.zezame.lipayz.repo;

import com.zezame.lipayz.model.History;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HistoryRepo extends JpaRepository<History, UUID> {
}
