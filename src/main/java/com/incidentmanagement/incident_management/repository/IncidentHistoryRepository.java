package com.incidentmanagement.incident_management.repository;

import com.incidentmanagement.incident_management.entity.IncidentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentHistoryRepository extends JpaRepository<IncidentHistory, Long> {
    List<IncidentHistory> findByIncidentIdOrderByChangedAtAscIdAsc(Long incidentId);
}
