package com.example.concurrencycontrolproject.domain.userTicket.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.concurrencycontrolproject.domain.ticket.entity.Ticket;
import com.example.concurrencycontrolproject.domain.userTicket.entity.UserTicket;

public interface UserTicketRepository extends JpaRepository<UserTicket, Long> {
	Optional<UserTicket> findByTicket(Ticket ticket);
}
