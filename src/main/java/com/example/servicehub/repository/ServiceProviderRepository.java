package com.example.servicehub.repository;

import com.example.servicehub.model.entity.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {

    List<ServiceProvider> findByLocationIgnoreCase(String location);

    List<ServiceProvider> findByServiceEntity_ServiceName(String serviceName);

    List<ServiceProvider> findByLocationIgnoreCaseAndServiceEntity_ServiceName(String location, String serviceName);

    @Query("SELECT sp FROM ServiceProvider AS sp ORDER BY sp.likesCount DESC ")
    List<ServiceProvider> findTopLikedProviders();
}
