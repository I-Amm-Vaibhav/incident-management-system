package com.incidentmanagement.incident_management.controller;

import com.incidentmanagement.incident_management.dto.CreateIncidentRequest;
import com.incidentmanagement.incident_management.dto.IncidentResponse;
import com.incidentmanagement.incident_management.dto.UpdateIncidentStatusRequest;
import com.incidentmanagement.incident_management.entity.Incident;
import com.incidentmanagement.incident_management.service.IncidentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/incidents")
public class IncidentController {

    private final IncidentService incidentService;
    public IncidentController(IncidentService incidentService){
        this.incidentService = incidentService;
    }

    @PostMapping
    public IncidentResponse createIncident(@RequestBody CreateIncidentRequest request){
        return incidentService.createIncident(request);
    }

    @GetMapping("/{id}")
    public IncidentResponse getIncidentById(@PathVariable Long id) {
        return incidentService.getIncidentById(id);
    }

    @PatchMapping("/{id}/status")
    public IncidentResponse updateIncidentStatus(
            @PathVariable Long id,
            @RequestBody UpdateIncidentStatusRequest request
    ) {
        return incidentService.updateStatus(id, request.getStatus());
    }
}
