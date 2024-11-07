package com.example.servicehub.service;

import com.example.servicehub.model.entity.ServiceEntity;
import com.example.servicehub.repository.ServiceRepository;
import org.springframework.stereotype.Service;

@Service
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public ServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public ServiceEntity findServiceByServiceName(String serviceName) {

        return serviceRepository.findByServiceName(serviceName).orElseThrow(() ->
                new RuntimeException("Service with name: " + serviceName +
                        " was not found!"));
    }

}
