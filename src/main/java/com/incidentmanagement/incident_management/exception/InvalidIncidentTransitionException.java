package com.incidentmanagement.incident_management.exception;

public class InvalidIncidentTransitionException extends RuntimeException{
    public InvalidIncidentTransitionException(String message){
        super(message);
    }
}
