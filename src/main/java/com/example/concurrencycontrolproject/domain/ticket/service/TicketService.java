package com.example.concurrencycontrolproject.domain.ticket.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.concurrencycontrolproject.domain.scheduleSeat.entity.ScheduleSeat;
import com.example.concurrencycontrolproject.domain.scheduleSeat.response.ScheduleSeatRepository;
import com.example.concurrencycontrolproject.domain.ticket.dto.response.TicketResponseDto;
import com.example.concurrencycontrolproject.domain.ticket.entity.Ticket;
import com.example.concurrencycontrolproject.domain.ticket.repository.TicketRepository;
import com.example.concurrencycontrolproject.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketService {

	private final TicketRepository ticketRepository;
	private final UserRepository userRepository;
	private final ScheduleSeatRepository scheduleSeatRepository;

	// 유저 검증
	private void findUser(Long userId) {
		userRepository.findById(userId)
			.orElseThrow(
				() -> new ResponseStatusException(
					HttpStatus.NOT_FOUND,
					"사용자를 찾을 수 없습니다."
				)
			);
	}

	// 좌석 검증
	private ScheduleSeat findScheduleSeat(Long scheduleSeatId) {
		return scheduleSeatRepository.findByIdAndAssignedIsFalse(scheduleSeatId)
			.orElseThrow(
				() -> new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"좌석을 선택할 수 없습니다."
				)
			);
	}

	// 티켓 검증
	private Ticket findTicket(Long ticketId) {
		return ticketRepository.findById(ticketId)
			.orElseThrow(
				() -> new ResponseStatusException(
					HttpStatus.NOT_FOUND,
					"티켓을 찾을 수 없습니다."
				)
			);
	}

	// 티켓 생성
	@Transactional
	public TicketResponseDto saveTicket(Long userId, Long scheduleSeatId) {

		// 유저 검증
		findUser(userId);

		// 좌석 검증
		ScheduleSeat scheduleSeat = findScheduleSeat(scheduleSeatId);

		// 티켓 생성
		Ticket ticket = Ticket.saveTicket(scheduleSeat);

		// 티켓 DB 저장
		Ticket savedTicket = ticketRepository.save(ticket);

		// 좌석 플래그 트루로 변경
		scheduleSeat.assign();

		return TicketResponseDto.ticketResponseDto(savedTicket);

	}

	// 티켓 단건 조회
	@Transactional(readOnly = true)
	public TicketResponseDto getTicket(Long userId, Long ticketId) {

		// 유저 검증
		findUser(userId);

		// 티켓 검증
		Ticket ticket = findTicket(ticketId);

		return TicketResponseDto.ticketResponseDto(ticket);
	}

	// 티켓 다건 조회
	@Transactional(readOnly = true)
	public Page<TicketResponseDto> getTickets(
		Long userId, Pageable pageable, Long concertId, String scheduleStatus, String ticketStatus,
		LocalDateTime startedAt, LocalDateTime endedAt) {

	}
}
