package com.incidentmanagement.incident_management.service;

import com.incidentmanagement.incident_management.entity.*;
import com.incidentmanagement.incident_management.exception.IncidentNotFoundException;
import com.incidentmanagement.incident_management.exception.InvalidIncidentTransitionException;
import com.incidentmanagement.incident_management.repository.IncidentRepository;
import com.incidentmanagement.incident_management.repository.ServiceRepository;
import com.incidentmanagement.incident_management.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.AfterEach;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private IncidentService incidentService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void mockAuthentication(User user) {

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        null
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
    }

    @Test
    void shouldUpdateIncidentStatus() {

        Incident incident = new Incident();
        incident.setStatus(IncidentStatus.OPEN);

        Service service = new Service();
        service.setId(1L);
        incident.setService(service);

        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setRole(Role.ENGINEER);

        incident.setReportedBy(user);
        incident.setAssignedTo(user);

        mockAuthentication(user);

        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        when(incidentRepository.save(incident))
                .thenReturn(incident);

        incidentService.updateStatus(1L, IncidentStatus.IN_PROGRESS);

        assertEquals(IncidentStatus.IN_PROGRESS, incident.getStatus());
        verify(incidentRepository).findById(1L);
        verify(incidentRepository).save(incident);
    }

    @Test
    void shouldThrowExceptionWhenIncidentDoesNotExist() {

        when(incidentRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                IncidentNotFoundException.class,
                () -> incidentService.updateStatus(99L, IncidentStatus.IN_PROGRESS)
        );

        verify(incidentRepository).findById(99L);
        verify(incidentRepository, never()).save(any());
    }

    @Test
    void shouldNotSaveWhenStatusTransitionIsInvalid() {

        Incident incident = new Incident();
        incident.setStatus(IncidentStatus.OPEN);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setRole(Role.ENGINEER);

        incident.setAssignedTo(user);

        mockAuthentication(user);

        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        assertThrows(
                InvalidIncidentTransitionException.class,
                () -> incidentService.updateStatus(1L, IncidentStatus.RESOLVED)
        );

        verify(incidentRepository).findById(1L);
        verify(incidentRepository, never()).save(any());
    }

    @Test
    void assignedEngineerShouldBeAllowedToUpdateIncidentStatus() {

        User engineer = new User();
        engineer.setId(1L);
        engineer.setEmail("engineer@example.com");
        engineer.setRole(Role.ENGINEER);

        Incident incident = new Incident();
        incident.setStatus(IncidentStatus.OPEN);
        incident.setAssignedTo(engineer);

        Service service = new Service();
        service.setId(1L);
        incident.setService(service);

        User reporter = new User();
        reporter.setId(2L);
        reporter.setName("Test Reporter");
        reporter.setEmail("reporter@example.com");
        reporter.setRole(Role.REPORTER);

        incident.setReportedBy(reporter);

        mockAuthentication(engineer);

        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        when(incidentRepository.save(incident))
                .thenReturn(incident);

        incidentService.updateStatus(1L, IncidentStatus.IN_PROGRESS);

        assertEquals(IncidentStatus.IN_PROGRESS, incident.getStatus());
        verify(incidentRepository).save(incident);
    }

    @Test
    void engineerShouldNotUpdateIncidentAssignedToAnotherEngineer() {

        User authenticatedEngineer = new User();
        authenticatedEngineer.setId(1L);
        authenticatedEngineer.setEmail("engineer1@example.com");
        authenticatedEngineer.setRole(Role.ENGINEER);

        User otherEngineer = new User();
        otherEngineer.setId(2L);
        otherEngineer.setEmail("engineer2@example.com");
        otherEngineer.setRole(Role.ENGINEER);

        Incident incident = new Incident();
        incident.setStatus(IncidentStatus.OPEN);
        incident.setAssignedTo(otherEngineer);

        mockAuthentication(authenticatedEngineer);

        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        assertThrows(
                AccessDeniedException.class,
                () -> incidentService.updateStatus(
                        1L,
                        IncidentStatus.IN_PROGRESS
                )
        );

        verify(incidentRepository, never()).save(any());
    }

    @Test
    void adminShouldBeAllowedToUpdateAnyIncident() {

        User admin = new User();
        admin.setId(1L);
        admin.setEmail("admin@example.com");
        admin.setRole(Role.ADMIN);

        User engineer = new User();
        engineer.setId(2L);
        engineer.setEmail("engineer@example.com");
        engineer.setRole(Role.ENGINEER);

        Incident incident = new Incident();
        incident.setStatus(IncidentStatus.OPEN);
        incident.setAssignedTo(engineer);

        Service service = new Service();
        service.setId(1L);
        incident.setService(service);

        User reporter = new User();
        reporter.setId(3L);
        reporter.setName("Test Reporter");
        reporter.setEmail("reporter@example.com");
        reporter.setRole(Role.REPORTER);

        incident.setReportedBy(reporter);

        mockAuthentication(admin);

        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        when(incidentRepository.save(incident))
                .thenReturn(incident);

        incidentService.updateStatus(1L, IncidentStatus.IN_PROGRESS);

        assertEquals(IncidentStatus.IN_PROGRESS, incident.getStatus());
        verify(incidentRepository).save(incident);
    }
}