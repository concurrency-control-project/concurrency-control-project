package com.example.concurrencycontrolproject.domain.ticket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.concurrencycontrolproject.domain.ticket.entity.Ticket;
import com.example.concurrencycontrolproject.domain.ticket.entity.TicketStatus;

public interface TicketRepository extends JpaRepository<Ticket, Long>, TicketRepositoryCustom {

	List<Ticket> findTicketsByStatus(TicketStatus status);

	// 예약 가능 상태 검증
	boolean existsByScheduleIdAndSeatIdAndStatusIn(Long scheduleId, Long seatId, List<TicketStatus> statuses);

	// 동시성 테스트 용 티켓 카운트 메서드
	long countByScheduleIdAndSeatIdAndStatus(Long scheduleId, Long seatId, TicketStatus ticketStatus);
}
