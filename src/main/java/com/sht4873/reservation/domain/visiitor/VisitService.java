package com.sht4873.reservation.domain.visiitor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VisitService {

    private final VisitRepository repository;

    @Autowired
    public VisitService(VisitRepository repository) {
        this.repository = repository;
    }

    public Visit reservation(Visit entity) {
        return repository.save(entity);
    }
}
