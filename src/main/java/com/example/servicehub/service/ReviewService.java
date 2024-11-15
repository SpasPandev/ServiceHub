package com.example.servicehub.service;

import com.example.servicehub.model.dto.ReviewRequestDto;
import com.example.servicehub.model.entity.Review;
import com.example.servicehub.model.entity.ServiceProvider;
import com.example.servicehub.model.entity.User;
import com.example.servicehub.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }


    public Review saveReviewByInfo(ReviewRequestDto reviewRequestDto, ServiceProvider serviceProvider, User currentUser) {

        return reviewRepository.save(Review.builder()
                .content(reviewRequestDto.getContent())
                .publishedAt(Timestamp.valueOf(LocalDateTime.now()))
                .serviceProvider(serviceProvider)
                .user(currentUser)
                .build());
    }
}
