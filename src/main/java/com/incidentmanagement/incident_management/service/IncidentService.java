package com.incidentmanagement.incident_management.service;

import com.incidentmanagement.incident_management.dto.CreateIncidentRequest;
import com.incidentmanagement.incident_management.dto.IncidentResponse;
import com.incidentmanagement.incident_management.dto.UserResponse;
import com.incidentmanagement.incident_management.entity.*;
import com.incidentmanagement.incident_management.exception.IncidentNotFoundException;
import com.incidentmanagement.incident_management.exception.ServiceNotFoundException;
import com.incidentmanagement.incident_management.repository.IncidentRepository;
import com.incidentmanagement.incident_management.repository.ServiceRepository;
import com.incidentmanagement.incident_management.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@org.springframework.stereotype.Service
public class IncidentService {
    private final IncidentRepository incidentRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    public IncidentService(IncidentRepository incidentRepository, ServiceRepository serviceRepository, UserRepository userRepository){
        this.incidentRepository = incidentRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
    }

    public IncidentResponse getIncidentById(Long id){
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException("Incident not found with id: " + id));

        IncidentResponse response = new IncidentResponse();
        response.setId(incident.getId());
        response.setTitle(incident.getTitle());
        response.setDescription(incident.getDescription());
        response.setSeverity(incident.getSeverity());
        response.setStatus(incident.getStatus());
        response.setServiceId(incident.getService().getId());

        UserResponse userResponse = new UserResponse();
        userResponse.setId(incident.getReportedBy().getId());
        userResponse.setName(incident.getReportedBy().getName());
        userResponse.setEmail(incident.getReportedBy().getEmail());
        userResponse.setRole(incident.getReportedBy().getRole());
        userResponse.setCreatedAt(incident.getReportedBy().getCreatedAt());
        response.setReportedBy(userResponse);

        return response;
    }

    public IncidentResponse createIncident(CreateIncidentRequest request){
        Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ServiceNotFoundException("Service not found with id: " +request.getServiceId()));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found"));

        Incident incident = new Incident();
        incident.setTitle(request.getTitle());
        incident.setDescription(request.getDescription());
        incident.setSeverity(request.getSeverity());
        incident.setService(service);
        incident.setReportedBy(user);

        Incident savedIncident = incidentRepository.save(incident);
        IncidentResponse response = new IncidentResponse();
        response.setId(savedIncident.getId());
        response.setTitle(savedIncident.getTitle());
        response.setDescription(savedIncident.getDescription());
        response.setSeverity(savedIncident.getSeverity());
        response.setStatus(savedIncident.getStatus());
        response.setServiceId(savedIncident.getService().getId());

        UserResponse userResponse = new UserResponse();
        userResponse.setId(savedIncident.getReportedBy().getId());
        userResponse.setName(savedIncident.getReportedBy().getName());
        userResponse.setEmail(savedIncident.getReportedBy().getEmail());
        userResponse.setRole(savedIncident.getReportedBy().getRole());
        userResponse.setCreatedAt(savedIncident.getReportedBy().getCreatedAt());
        response.setReportedBy(userResponse);

        return response;
    }

    @PreAuthorize("hasAnyRole('ENGINEER', 'ADMIN')")
    public IncidentResponse updateStatus(Long incidentId, IncidentStatus newStatus) {

        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() ->
                        new IncidentNotFoundException(
                                "Incident not found with id: " + incidentId
                        ));

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Authenticated user not found")
                );

        if (currentUser.getRole() == Role.ENGINEER) {
            if (incident.getAssignedTo() == null ||
                    !incident.getAssignedTo().getId().equals(currentUser.getId())) {

                throw new AccessDeniedException(
                        "Engineer is not assigned to this incident"
                );
            }
        }

        incident.transitionTo(newStatus);

        Incident savedIncident = incidentRepository.save(incident);

        return mapToResponse(savedIncident);
    }

    private IncidentResponse mapToResponse(Incident incident) {

        IncidentResponse response = new IncidentResponse();

        response.setId(incident.getId());
        response.setTitle(incident.getTitle());
        response.setDescription(incident.getDescription());
        response.setSeverity(incident.getSeverity());
        response.setStatus(incident.getStatus());
        response.setServiceId(incident.getService().getId());

        UserResponse userResponse = new UserResponse();
        userResponse.setId(incident.getReportedBy().getId());
        userResponse.setName(incident.getReportedBy().getName());
        userResponse.setEmail(incident.getReportedBy().getEmail());
        userResponse.setRole(incident.getReportedBy().getRole());
        userResponse.setCreatedAt(incident.getReportedBy().getCreatedAt());

        response.setReportedBy(userResponse);

        return response;
    }
}
