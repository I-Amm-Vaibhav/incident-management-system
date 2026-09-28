package com.incidentmanagement.incident_management.controller;

import com.incidentmanagement.incident_management.dto.IncidentResponse;
import com.incidentmanagement.incident_management.entity.IncidentStatus;
import com.incidentmanagement.incident_management.service.IncidentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(IncidentController.class)
public class IncidentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IncidentService incidentService;

    @Test
    void shouldUpdateIncidentStatus() throws Exception {

        IncidentResponse response = new IncidentResponse();
        response.setId(1L);
        response.setStatus(IncidentStatus.IN_PROGRESS);

        when(incidentService.updateStatus(1L, IncidentStatus.IN_PROGRESS))
                .thenReturn(response);

        mockMvc.perform(
                        patch("/incidents/1/status")
                                .contentType("application/json")
                                .content("""
                                {
                                    "status": "IN_PROGRESS"
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
        verify(incidentService).updateStatus(1L, IncidentStatus.IN_PROGRESS);
    }

    @Test
    void shouldRejectInvalidIncidentStatus() throws Exception {

        mockMvc.perform(
                        patch("/incidents/1/status")
                                .contentType("application/json")
                                .content("""
                            {
                                "status": "INVALID_STATUS"
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(incidentService, never())
                .updateStatus(1L, null);
    }
}