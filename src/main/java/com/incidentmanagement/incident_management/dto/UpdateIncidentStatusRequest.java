package com.incidentmanagement.incident_management.dto;

import com.incidentmanagement.incident_management.entity.IncidentStatus;

public class UpdateIncidentStatusRequest {
    private IncidentStatus status;

    public IncidentStatus getStatus(){
        return status;
    }

    public void setStatus(IncidentStatus status){
        this.status = status;
    }
}
