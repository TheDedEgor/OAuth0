package com.example.telegramauth.service;

import com.example.telegramauth.exception.ExpiredTimeUuidException;
import com.example.telegramauth.exception.NotFoundServiceParamsException;
import org.springframework.stereotype.Service;

import com.example.telegramauth.model.dto.ServiceParamsDTO;
import com.example.telegramauth.model.entity.ServiceParams;
import com.example.telegramauth.repository.ServiceParamsRepository;

import jakarta.transaction.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class ParamsService {

    private final ServiceParamsRepository serviceParamsRepository;

    public ParamsService(ServiceParamsRepository serviceParamsRepository) {
        this.serviceParamsRepository = serviceParamsRepository;
    }

    @Transactional
    public String create(ServiceParamsDTO serviceParamsDTO) {
        var serviceParams = new ServiceParams(serviceParamsDTO);
        return serviceParamsRepository.save(serviceParams).getUuid();
    }

    public ServiceParams get(String uuid) throws NotFoundServiceParamsException, ExpiredTimeUuidException {
       return get(uuid, false);
    }

    public ServiceParams get(String uuid, Boolean checkTime) throws NotFoundServiceParamsException, ExpiredTimeUuidException {
        var serviceParamsOptional = serviceParamsRepository.findById(uuid);
        if (serviceParamsOptional.isEmpty()) {
            throw new NotFoundServiceParamsException("Данный uuid не найден");
        }
        var serviceParams = serviceParamsOptional.get();

        if (checkTime) {
            var createdAt = serviceParams.getCreatedAt();
            var duration = Duration.between(createdAt, LocalDateTime.now());
            if (duration.toMinutes() > 1) {
                throw new ExpiredTimeUuidException("Время действия uuid истекло");
            }
        }

        return serviceParams;
    }
}
