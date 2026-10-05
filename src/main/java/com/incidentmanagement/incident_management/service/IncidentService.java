package com.incidentmanagement.incident_management.service;

import com.incidentmanagement.incident_management.dto.*;
import com.incidentmanagement.incident_management.entity.*;
import com.incidentmanagement.incident_management.exception.IncidentNotFoundException;
import com.incidentmanagement.incident_management.exception.InvalidAssignmentException;
import com.incidentmanagement.incident_management.exception.ResourceNotFoundException;
import com.incidentmanagement.incident_management.exception.ServiceNotFoundException;
import com.incidentmanagement.incident_management.repository.IncidentCommentRepository;
import com.incidentmanagement.incident_management.repository.IncidentRepository;
import com.incidentmanagement.incident_management.repository.ServiceRepository;
import com.incidentmanagement.incident_management.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@org.springframework.stereotype.Service
public class IncidentService {
    private final IncidentRepository incidentRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final IncidentCommentRepository incidentCommentRepository;

    public IncidentService(IncidentRepository incidentRepository, ServiceRepository serviceRepository, UserRepository userRepository, IncidentCommentRepository incidentCommentRepository){
        this.incidentRepository = incidentRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
        this.incidentCommentRepository = incidentCommentRepository;
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

        UserResponse reportedBy = new UserResponse();
        reportedBy.setId(incident.getReportedBy().getId());
        reportedBy.setName(incident.getReportedBy().getName());
        reportedBy.setEmail(incident.getReportedBy().getEmail());
        reportedBy.setRole(incident.getReportedBy().getRole());
        reportedBy.setCreatedAt(incident.getReportedBy().getCreatedAt());

        response.setReportedBy(reportedBy);

        if (incident.getAssignedTo() != null) {
            UserResponse assignedTo = new UserResponse();
            assignedTo.setId(incident.getAssignedTo().getId());
            assignedTo.setName(incident.getAssignedTo().getName());
            assignedTo.setEmail(incident.getAssignedTo().getEmail());
            assignedTo.setRole(incident.getAssignedTo().getRole());
            assignedTo.setCreatedAt(incident.getAssignedTo().getCreatedAt());

            response.setAssignedTo(assignedTo);
        }

        return response;
    }

    private CommentResponse mapToResponse(IncidentComment comment) {
        CommentResponse response = new CommentResponse();

        response.setId(comment.getId());
        response.setComment(comment.getComment());
        response.setIncidentId(comment.getIncident().getId());

        UserResponse userResponse = new UserResponse();
        userResponse.setId(comment.getUser().getId());
        userResponse.setName(comment.getUser().getName());
        userResponse.setEmail(comment.getUser().getEmail());
        userResponse.setRole(comment.getUser().getRole());
        userResponse.setCreatedAt(comment.getUser().getCreatedAt());

        response.setUser(userResponse);
        response.setCreatedAt(comment.getCreatedAt());

        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<IncidentResponse> getUnassignedIncidents(){
        return incidentRepository.findByAssignedToIsNull()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public IncidentResponse assignIncident(Long incidentId, Long engineerId){
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new IncidentNotFoundException("Incident not found with incident id: " + incidentId));

        User engineer = userRepository.findById(engineerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + engineerId));

        if(engineer.getRole() != Role.ENGINEER){
            throw new InvalidAssignmentException("User is not a engineer");
        }

        incident.setAssignedTo(engineer);
        Incident savedIncident = incidentRepository.save(incident);

        return mapToResponse(savedIncident);
    }

    public CommentResponse createComment(
            CreateCommentRequest request,
            Long id) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email " + email));

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() ->
                        new IncidentNotFoundException(
                                "Incident not found with incident id: " + id));

        validateCommentAccess(incident, currentUser);

        IncidentComment comment = new IncidentComment();

        comment.setComment(request.getComment());
        comment.setIncident(incident);
        comment.setUser(currentUser);

        IncidentComment savedComment =
                incidentCommentRepository.save(comment);

        return mapToResponse(savedComment);
    }

    private void validateCommentAccess(Incident incident, User currentUser) {

        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }

        if (currentUser.getRole() == Role.ENGINEER
                && incident.getAssignedTo() != null
                && incident.getAssignedTo().getId().equals(currentUser.getId())) {
            return;
        }

        if (currentUser.getRole() == Role.REPORTER
                && incident.getReportedBy().getId().equals(currentUser.getId())) {
            return;
        }

        throw new AccessDeniedException(
                "You do not have permission to comment on this incident");
    }

    public List<CommentResponse> getComments(Long incidentId) {

        incidentRepository.findById(incidentId)
                .orElseThrow(() ->
                        new IncidentNotFoundException(
                                "Incident not found with incident id: " + incidentId));

        return incidentCommentRepository
                .findByIncidentIdOrderByCreatedAtAsc(incidentId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
}
