package com.incidentmanagement.incident_management.dto;

import com.incidentmanagement.incident_management.entity.IncidentStatus;
import com.incidentmanagement.incident_management.entity.Severity;

public class IncidentResponse {
    private Long id;
    private String title;
    private String description;
    private Severity severity;
    private IncidentStatus status;
    private Long serviceId;
    private UserResponse reportedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public void setStatus(IncidentStatus status) {
        this.status = status;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public UserResponse getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(UserResponse reportedBy) {
        this.reportedBy = reportedBy;
    }
}
