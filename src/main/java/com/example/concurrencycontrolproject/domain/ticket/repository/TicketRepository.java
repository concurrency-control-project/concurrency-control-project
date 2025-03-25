package com.example.concurrencycontrolproject.domain.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.concurrencycontrolproject.domain.ticket.entity.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

}
