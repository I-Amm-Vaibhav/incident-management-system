package com.incidentmanagement.incident_management.service;

import com.incidentmanagement.incident_management.entity.IncidentStatus;
import com.incidentmanagement.incident_management.repository.IncidentRepository;
import com.incidentmanagement.incident_management.repository.ServiceRepository;
import com.incidentmanagement.incident_management.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = IncidentServiceSecurityTest.TestConfig.class)
class IncidentServiceSecurityTest {

    @Autowired
    private IncidentService incidentService;

    @MockitoBean
    private IncidentRepository incidentRepository;

    @MockitoBean
    private ServiceRepository serviceRepository;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(roles = "REPORTER")
    void reporterShouldNotBeAllowedToUpdateIncidentStatus() {

        assertThrows(
                AccessDeniedException.class,
                () -> incidentService.updateStatus(
                        1L,
                        IncidentStatus.IN_PROGRESS
                )
        );

        verify(incidentRepository, never()).findById(1L);
    }

    @Configuration
    @EnableMethodSecurity
    static class TestConfig {

        @Bean
        IncidentService incidentService(
                IncidentRepository incidentRepository,
                ServiceRepository serviceRepository,
                UserRepository userRepository
        ) {
            return new IncidentService(
                    incidentRepository,
                    serviceRepository,
                    userRepository
            );
        }
    }
}