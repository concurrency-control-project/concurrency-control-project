package com.example.concurrencycontrolproject.domain.canceledticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.concurrencycontrolproject.domain.canceledticket.entity.CanceledTicket;

public interface CanceledTicketRepository extends JpaRepository<CanceledTicket, Long>, CanceledTicketRepositoryCustom {

}
