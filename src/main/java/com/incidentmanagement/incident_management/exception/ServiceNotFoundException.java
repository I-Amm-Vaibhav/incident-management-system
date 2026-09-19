package com.incidentmanagement.incident_management.exception;

public class ServiceNotFoundException extends RuntimeException{
    public ServiceNotFoundException(String message){
        super(message);
    }
}
