package com.incidentmanagement.incident_management.controller;

import com.incidentmanagement.incident_management.dto.CreateServiceRequest;
import com.incidentmanagement.incident_management.entity.Service;
import com.incidentmanagement.incident_management.service.ServiceManagementService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/services")
public class ServiceController {
    private final ServiceManagementService serviceManagementService;
    public ServiceController(ServiceManagementService serviceManagementService){
        this.serviceManagementService = serviceManagementService;
    }

    @PostMapping
    public Service createService(@RequestBody CreateServiceRequest request){
        return serviceManagementService.createService(request);
    }
}
