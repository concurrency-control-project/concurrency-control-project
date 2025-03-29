package com.example.concurrencycontrolproject.domain.userTicket.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.concurrencycontrolproject.domain.ticket.entity.Ticket;
import com.example.concurrencycontrolproject.domain.user.entity.User;
import com.example.concurrencycontrolproject.domain.userTicket.entity.UserTicket;

public interface UserTicketRepository extends JpaRepository<UserTicket, Long> {
	Optional<UserTicket> findByTicket(Ticket ticket);

	// 티켓 삭제 테스트 확인용
	boolean existsByTicketId(Long ticketId);

	// 티켓 재예매 테스트 확인용
	boolean existsByUserAndTicket(User user2, Ticket newTicket);
}
