package com.example.servicehub.service;

import com.example.servicehub.exception.ServiceProviderNotFoundException;
import com.example.servicehub.model.entity.ServiceProvider;
import com.example.servicehub.repository.ServiceProviderRepository;
import org.springframework.stereotype.Service;

@Service
public class ServiceProviderService {

    private final ServiceProviderRepository serviceProviderRepository;

    public ServiceProviderService(ServiceProviderRepository serviceProviderRepository) {
        this.serviceProviderRepository = serviceProviderRepository;
    }


    public void saveServiceProvider(ServiceProvider serviceProvider) {

        serviceProviderRepository.save(serviceProvider);
    }

    public ServiceProvider findServiceProviderById(Long serviceProviderId) {

        return serviceProviderRepository.findById(serviceProviderId).orElseThrow(() ->
                new ServiceProviderNotFoundException("ServiceProvider with id: " + serviceProviderId +
                        " was not found!"));
    }

    public ServiceProvider likeServiceProvider(Long serviceProviderId) {

        ServiceProvider serviceProvider = serviceProviderRepository.findById(serviceProviderId).orElseThrow(() ->
                new ServiceProviderNotFoundException("ServiceProvider with id: " + serviceProviderId +
                        " was not found!"));

        serviceProvider.setLikesCount(serviceProvider.getLikesCount() + 1);
        serviceProviderRepository.save(serviceProvider);

        return serviceProvider;
    }
}
