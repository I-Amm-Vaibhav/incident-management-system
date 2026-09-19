package com.incidentmanagement.incident_management.exception;

public class IncidentNotFoundException extends RuntimeException{
    public IncidentNotFoundException(String message){
        super(message);
    }
}
