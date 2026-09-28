package com.incidentmanagement.incident_management.entity;

import com.incidentmanagement.incident_management.exception.InvalidIncidentTransitionException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class IncidentTest {
    @Test
    void shouldTransitionFromOpenToInProgress() {
        Incident incident = new Incident();
        incident.setStatus(IncidentStatus.OPEN);

        incident.transitionTo(IncidentStatus.IN_PROGRESS);

        assertEquals(IncidentStatus.IN_PROGRESS, incident.getStatus());
    }

    @Test
    void shouldTransitionFromInProgressToResolved(){
        Incident incident = new Incident();
        incident.setStatus(IncidentStatus.IN_PROGRESS);
        incident.transitionTo(IncidentStatus.RESOLVED);
        assertEquals(IncidentStatus.RESOLVED, incident.getStatus());
    }

    @Test
    void shouldTransitionFromResolvedToClosed() {
        Incident incident = new Incident();
        incident.setStatus(IncidentStatus.RESOLVED);

        incident.transitionTo(IncidentStatus.CLOSED);

        assertEquals(IncidentStatus.CLOSED, incident.getStatus());
    }

    @Test
    void shouldNotTransitionFromOpenToResolved() {
        Incident incident = new Incident();
        incident.setStatus(IncidentStatus.OPEN);

        assertThrows(
                InvalidIncidentTransitionException.class,
                () -> incident.transitionTo(IncidentStatus.RESOLVED)
        );
    }

    @Test
    void shouldNotTransitionFromOpenToClosed() {
        Incident incident = new Incident();
        incident.setStatus(IncidentStatus.OPEN);

        assertThrows(
                InvalidIncidentTransitionException.class,
                () -> incident.transitionTo(IncidentStatus.CLOSED)
        );
    }

    @Test
    void shouldNotTransitionFromInProgressToClosed() {
        Incident incident = new Incident();
        incident.setStatus(IncidentStatus.IN_PROGRESS);

        assertThrows(
                InvalidIncidentTransitionException.class,
                () -> incident.transitionTo(IncidentStatus.CLOSED)
        );
    }

    @Test
    void shouldNotTransitionFromResolvedToInProgress() {
        Incident incident = new Incident();
        incident.setStatus(IncidentStatus.RESOLVED);

        assertThrows(
                InvalidIncidentTransitionException.class,
                () -> incident.transitionTo(IncidentStatus.IN_PROGRESS)
        );
    }

    @Test
    void shouldNotTransitionFromClosedToOpen() {
        Incident incident = new Incident();
        incident.setStatus(IncidentStatus.CLOSED);

        assertThrows(
                InvalidIncidentTransitionException.class,
                () -> incident.transitionTo(IncidentStatus.OPEN)
        );
    }

    @Test
    void shouldNotTransitionFromClosedToInProgress() {
        Incident incident = new Incident();
        incident.setStatus(IncidentStatus.CLOSED);

        assertThrows(
                InvalidIncidentTransitionException.class,
                () -> incident.transitionTo(IncidentStatus.IN_PROGRESS)
        );
    }

    @Test
    void shouldSetResolvedAtWhenIncidentIsResolved() {
        Incident incident = new Incident();
        incident.setStatus(IncidentStatus.IN_PROGRESS);

        assertNull(incident.getResolvedAt());

        incident.transitionTo(IncidentStatus.RESOLVED);

        assertEquals(IncidentStatus.RESOLVED, incident.getStatus());
        assertNotNull(incident.getResolvedAt());
    }

    @Test
    void shouldPreserveResolvedAtWhenIncidentIsClosed() {
        Incident incident = new Incident();

        LocalDateTime resolvedTime = LocalDateTime.now().minusMinutes(10);

        incident.setStatus(IncidentStatus.RESOLVED);
        incident.setResolvedAt(resolvedTime);

        incident.transitionTo(IncidentStatus.CLOSED);

        assertEquals(IncidentStatus.CLOSED, incident.getStatus());
        assertEquals(resolvedTime, incident.getResolvedAt());
    }
}
