package com.incidentmanagement.incident_management.repository;

import com.incidentmanagement.incident_management.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Long> {
}
