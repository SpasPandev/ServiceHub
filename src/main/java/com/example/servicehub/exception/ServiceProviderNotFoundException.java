package com.example.servicehub.exception;

public class ServiceProviderNotFoundException extends RuntimeException {
    public ServiceProviderNotFoundException(String message){

        super(message);
    }

}
