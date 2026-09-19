package com.incidentmanagement.incident_management.repository;

import com.incidentmanagement.incident_management.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
}
