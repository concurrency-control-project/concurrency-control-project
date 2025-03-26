package com.example.concurrencycontrolproject.domain.seat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.concurrencycontrolproject.domain.seat.entity.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {

}
