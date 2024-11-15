package com.example.servicehub.repository;

import com.example.servicehub.model.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByServiceProvider_IdOrderByPublishedAtDesc(Long serviceProviderId);

}
