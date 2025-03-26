package com.example.concurrencycontrolproject.domain.seat.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.concurrencycontrolproject.domain.seat.entity.ScheduledSeat;

@Repository
public interface ScheduledSeatRepository extends CrudRepository<ScheduledSeat, String> {
}
