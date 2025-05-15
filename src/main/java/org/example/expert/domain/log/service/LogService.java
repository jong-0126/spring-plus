package org.example.expert.domain.log.service;

import org.example.expert.domain.log.entity.Log;
import org.example.expert.domain.log.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class LogService {

    private final LogRepository logRepository;


    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(String action, String details, Long userId) {
        Log log = new Log();
        log.setRequestTime(LocalDateTime.now());
        log.setAction(action);
        log.setMessage(details);
        log.setUserId(userId);
        logRepository.save(log);
    }
}
