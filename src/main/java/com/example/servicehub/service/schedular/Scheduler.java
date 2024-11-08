package com.example.servicehub.service.schedular;

import com.example.servicehub.service.TokenService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class Scheduler {

    private final TokenService tokenService;

    public Scheduler(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    // every hour
    @Scheduled(cron = "0 0 * ? * *")
    @Transactional
    public void deleteAllInvalidTokens() {

        tokenService.deleteAllInvalidTokens();
    }

}
