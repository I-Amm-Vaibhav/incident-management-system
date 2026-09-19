package com.incidentmanagement.incident_management.service;

import com.incidentmanagement.incident_management.dto.CreateServiceRequest;
import com.incidentmanagement.incident_management.entity.Service;
import com.incidentmanagement.incident_management.repository.ServiceRepository;


@org.springframework.stereotype.Service
public class ServiceManagementService {
    private final ServiceRepository serviceRepository;

    public ServiceManagementService(ServiceRepository serviceRepository){
        this.serviceRepository = serviceRepository;
    }

    public Service createService(CreateServiceRequest request){
        Service service = new Service();
        service.setName(request.getName());
        service.setDescription(request.getDescription());

        return serviceRepository.save(service);
    }
}
