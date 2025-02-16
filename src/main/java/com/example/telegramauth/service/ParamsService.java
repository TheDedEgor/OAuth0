package com.example.telegramauth.service;

import com.example.telegramauth.model.dto.ServiceParamsDTO;
import com.example.telegramauth.model.entity.ServiceParams;
import com.example.telegramauth.repository.ServiceParamsRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ParamsService {

    private final ServiceParamsRepository serviceParamsRepository;

    public ParamsService(ServiceParamsRepository serviceParamsRepository) {
        this.serviceParamsRepository = serviceParamsRepository;
    }

    @Transactional
    public Long create(ServiceParamsDTO serviceParamsDTO) {
        var optionalServiceParams = serviceParamsRepository.findByAuthUrl(serviceParamsDTO.getAuthUrl());
        var serviceParams = optionalServiceParams.orElseGet(() -> new ServiceParams(serviceParamsDTO));
        return serviceParamsRepository.save(serviceParams).getId();
    }

    public ServiceParams get(Long id) {
        return serviceParamsRepository.findById(id).get();
    }
}
