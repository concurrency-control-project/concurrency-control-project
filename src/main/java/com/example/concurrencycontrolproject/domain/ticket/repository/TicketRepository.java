package com.example.concurrencycontrolproject.domain.ticket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.concurrencycontrolproject.domain.ticket.entity.Ticket;
import com.example.concurrencycontrolproject.domain.ticket.entity.TicketStatus;

public interface TicketRepository extends JpaRepository<Ticket, Long>, TicketRepositoryCustom {

	List<Ticket> findTicketsByStatus(TicketStatus status);

}
