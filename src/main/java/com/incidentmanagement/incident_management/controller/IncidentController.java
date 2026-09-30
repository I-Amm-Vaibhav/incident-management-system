package com.incidentmanagement.incident_management.controller;

import com.incidentmanagement.incident_management.dto.AssignIncidentRequest;
import com.incidentmanagement.incident_management.dto.CreateIncidentRequest;
import com.incidentmanagement.incident_management.dto.IncidentResponse;
import com.incidentmanagement.incident_management.dto.UpdateIncidentStatusRequest;
import com.incidentmanagement.incident_management.entity.Incident;
import com.incidentmanagement.incident_management.service.IncidentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/unassigned")
    public List<IncidentResponse> getUnassignedIncidents(){
        return incidentService.getUnassignedIncidents();
    }

    @PatchMapping("/{id}/assignment")
    public IncidentResponse assignIncident(@PathVariable Long id, @RequestBody AssignIncidentRequest request){
        return incidentService.assignIncident(id, request.getEngineerId());
    }
}
