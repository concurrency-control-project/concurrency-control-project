package com.example.concurrencycontrolproject.domain.ticket.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.concurrencycontrolproject.domain.common.auth.AuthUser;
import com.example.concurrencycontrolproject.domain.scheduleSeat.entity.ScheduleSeat;
import com.example.concurrencycontrolproject.domain.scheduleSeat.response.ScheduleSeatRepository;
import com.example.concurrencycontrolproject.domain.ticket.dto.request.TicketChangeRequestDto;
import com.example.concurrencycontrolproject.domain.ticket.dto.response.TicketResponseDto;
import com.example.concurrencycontrolproject.domain.ticket.entity.Ticket;
import com.example.concurrencycontrolproject.domain.ticket.entity.TicketStatus;
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
	public TicketResponseDto saveTicket(AuthUser authUser, Long scheduleSeatId) {

		// 유저 검증
		findUser(authUser.getId());

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
	public TicketResponseDto getTicket(AuthUser authUser, Long ticketId) {

		// 유저 검증
		findUser(authUser.getId());

		// 티켓 검증
		Ticket ticket = findTicket(ticketId);

		return TicketResponseDto.ticketResponseDto(ticket);
	}

	// 티켓 다건 조회
	@Transactional(readOnly = true)
	public Page<TicketResponseDto> getTickets(
		AuthUser authUser, Pageable pageable, Long scheduleId, String ticketStatus,
		LocalDateTime startedAt, LocalDateTime endedAt) {

		// 유저 검증
		findUser(authUser.getId());

		// 페이지 -1
		Pageable convertPageable = PageRequest.of(
			pageable.getPageNumber() - 1,
			pageable.getPageSize(),
			pageable.getSort()
		);

		return ticketRepository.findTickets(authUser.getId(), convertPageable, scheduleId, ticketStatus,
			startedAt, endedAt);

	}

	// 티켓 취소
	public void deleteTicket(AuthUser authUser, Long ticketId) {

		// 유저 검증
		findUser(authUser.getId());

		// 티켓 검증
		Ticket ticket = findTicket(ticketId);

		// 티켓 삭제 (소프트 딜리트)
		ticket.cancel();

		// 삭제된 정보 DB에 저장
		ticketRepository.save(ticket);
	}

	// 티켓 좌석 변경
	public TicketResponseDto updateTicket(AuthUser authUser, Long ticketId, TicketChangeRequestDto requestDto) {
		// 유저 검증
		findUser(authUser.getId());

		// 티켓 검증
		Ticket ticket = findTicket(ticketId);

		// 티켓 상태 검증
		if (ticket.getStatus() != TicketStatus.RESERVED) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "좌석 변경할 수 없습니다.");
		}

		ScheduleSeat oldScheduleSeat = ticket.getScheduleSeat();
		Long newScheduleSeatId = requestDto.getScheduleSeatId();

		// 좌석 검증
		ScheduleSeat newScheduleSeat = findScheduleSeat(newScheduleSeatId);

		// 기존 좌석 반환
		oldScheduleSeat.unassign();

		// 새 좌석 예약
		newScheduleSeat.assign();

		// 좌석 변경
		ticket.changeScheduleSeat(newScheduleSeat);

		// 기존 좌석 정보 변경, 새 좌석 정보 변경 DB 저장
		scheduleSeatRepository.save(oldScheduleSeat);
		scheduleSeatRepository.save(newScheduleSeat);

		// 티켓 저장
		Ticket updatedTicket = ticketRepository.save(ticket);

		return TicketResponseDto.ticketResponseDto(updatedTicket);
	}

	// 스케줄링 메서드
	@Scheduled(cron = "0 * * * * *")
	public void expireTicket() {
		List<Ticket> tickets = ticketRepository.findTicketsByStatus(TicketStatus.RESERVED);

		for (Ticket ticket : tickets) {
			if (Objects.equals(ticket.getScheduleSeat().getSchedule().getStatus().toString(), "started")) {
				ticket.expire();
			}
		}
	}
}
