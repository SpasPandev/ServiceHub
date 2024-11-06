package com.example.servicehub.service;

import com.example.servicehub.model.dto.ServiceCategoryDto;
import com.example.servicehub.model.dto.ServiceCategoryRequestDto;
import com.example.servicehub.model.dto.ServiceDto;
import com.example.servicehub.model.dto.ServiceRequestDto;
import com.example.servicehub.model.entity.ServiceCategory;
import com.example.servicehub.repository.ServiceCategoryRepository;
import com.example.servicehub.repository.ServiceRepository;
import jakarta.persistence.EntityExistsException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;

@Service
public class AdminService {

    private final ServiceCategoryRepository serviceCategoryRepository;
    private final ServiceRepository serviceRepository;
    private final ModelMapper modelMapper;

    public AdminService(ServiceCategoryRepository serviceCategoryRepository, ServiceRepository serviceRepository, ModelMapper modelMapper) {
        this.serviceCategoryRepository = serviceCategoryRepository;
        this.serviceRepository = serviceRepository;
        this.modelMapper = modelMapper;
    }


    public ResponseEntity<ServiceCategoryDto> createServiceCategory(
            ServiceCategoryRequestDto serviceCategoryRequestDto) {

        Optional<ServiceCategory> serviceCategoryOpt =
                serviceCategoryRepository.findByName(serviceCategoryRequestDto.getName());

        if (serviceCategoryOpt.isPresent()) {

            throw new EntityExistsException("ServiceCategory with name: " +
                    serviceCategoryRequestDto.getName() + " already exist!");
        }

        ServiceCategory savedServiceCategory = serviceCategoryRepository
                .save(ServiceCategory
                        .builder()
                        .name(serviceCategoryRequestDto.getName())
                        .services(new HashSet<>())
                        .build());

        return new ResponseEntity<>(modelMapper.map(savedServiceCategory, ServiceCategoryDto.class),
                HttpStatus.CREATED);
    }

    public ResponseEntity<ServiceDto> createService(ServiceRequestDto serviceRequestDto) {

        Optional<com.example.servicehub.model.entity.Service> serviceOpt =
                serviceRepository.findByServiceName(serviceRequestDto.getServiceName());

        if (serviceOpt.isPresent()) {

            throw new EntityExistsException("Service with name: " +
                    serviceRequestDto.getServiceName() + " already exist!");
        }

        Optional<ServiceCategory> serviceCategoryOpt =
                serviceCategoryRepository.findByName(serviceRequestDto.getServiceCategoryName());

        if (serviceCategoryOpt.isEmpty()) {

            throw new EntityExistsException("ServiceCategory with name: " +
                    serviceRequestDto.getServiceCategoryName() + " did not exist!");

        }

        com.example.servicehub.model.entity.Service savedService =
                serviceRepository.save(new com.example.servicehub.model.entity.Service()
                        .builder()
                        .serviceName(serviceRequestDto.getServiceName())
                        .serviceCategory(serviceCategoryOpt.get())
                        .serviceProviders(new HashSet<>())
                        .build());

        return new ResponseEntity<>(modelMapper.map(savedService, ServiceDto.class), HttpStatus.CREATED);
    }
}
