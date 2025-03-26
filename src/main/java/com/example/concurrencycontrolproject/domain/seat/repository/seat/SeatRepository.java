package com.example.concurrencycontrolproject.domain.seat.repository.seat;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.concurrencycontrolproject.domain.seat.entity.seat.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {

}
