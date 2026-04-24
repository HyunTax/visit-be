package com.sht4873.reservation.domain.visitor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    Optional<Visit> findByNameAndPhoneNum(String name, String phoneNum);
    List<Visit> findByPhoneNumIn(List<String> phoneNums);
    List<Visit> findAllByStatusIn(Collection<Visit.Status> statuses);
}