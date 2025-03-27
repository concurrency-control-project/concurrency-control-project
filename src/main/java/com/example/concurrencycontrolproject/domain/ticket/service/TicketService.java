package com.example.concurrencycontrolproject.domain.ticket.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.concurrencycontrolproject.domain.common.auth.AuthUser;
import com.example.concurrencycontrolproject.domain.scheduleSeat.entity.ScheduleSeat;
import com.example.concurrencycontrolproject.domain.scheduleSeat.response.ScheduleSeatRepository;
import com.example.concurrencycontrolproject.domain.ticket.dto.request.TicketChangeRequest;
import com.example.concurrencycontrolproject.domain.ticket.dto.response.TicketResponse;
import com.example.concurrencycontrolproject.domain.ticket.entity.Ticket;
import com.example.concurrencycontrolproject.domain.ticket.entity.TicketStatus;
import com.example.concurrencycontrolproject.domain.ticket.exception.TicketErrorCode;
import com.example.concurrencycontrolproject.domain.ticket.exception.TicketException;
import com.example.concurrencycontrolproject.domain.ticket.repository.TicketRepository;
import com.example.concurrencycontrolproject.domain.user.entity.User;
import com.example.concurrencycontrolproject.domain.user.repository.UserRepository;
import com.example.concurrencycontrolproject.domain.userTicket.entity.UserTicket;
import com.example.concurrencycontrolproject.domain.userTicket.repository.UserTicketRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketService {

	private final TicketRepository ticketRepository;
	private final UserRepository userRepository;
	private final ScheduleSeatRepository scheduleSeatRepository;
	private final UserTicketRepository userTicketRepository;

	// 유저 검증
	private User findUser(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(
				() -> new TicketException(TicketErrorCode.USER_NOT_FOUND));
	}

	// 좌석 검증
	private ScheduleSeat findScheduleSeat(Long scheduleSeatId) {
		return scheduleSeatRepository.findByIdAndAssignedIsFalse(scheduleSeatId)
			.orElseThrow(
				() -> new TicketException(TicketErrorCode.SCHEDULE_SEAT_BAD_REQUEST));
	}

	// 티켓 검증
	private Ticket findTicket(Long ticketId) {
		return ticketRepository.findById(ticketId)
			.orElseThrow(
				() -> new TicketException(TicketErrorCode.TICKET_NOT_FOUND));
	}

	// 티켓 소유 확인
	private UserTicket findUserTicketOwner(Long ticketId, Long userId) {

		// 티켓 검증
		Ticket ticket = findTicket(ticketId);

		// 유저티켓 정보 조회
		UserTicket userTicket = userTicketRepository.findByTicket(ticket)
			.orElseThrow(
				() -> new TicketException(TicketErrorCode.TICKET_OWNER_MAPPING_NOT_FOUND));

		// 유저티켓 소유 확인
		if (!userTicket.getUser().getId().equals(userId)) {
			throw new TicketException(TicketErrorCode.TICKET_ACCESS_DENIED);
		}
		return userTicket;
	}

	// 티켓 생성
	@Transactional
	public TicketResponse saveTicket(AuthUser authUser, Long scheduleSeatId) {

		// 유저 검증
		User user = findUser(authUser.getId());

		// 좌석 검증
		ScheduleSeat scheduleSeat = findScheduleSeat(scheduleSeatId);

		// 티켓 생성
		Ticket ticket = Ticket.saveTicket(scheduleSeat);

		// 티켓 DB 저장
		Ticket savedTicket = ticketRepository.save(ticket);

		// 유저티켓 맵핑
		UserTicket userTicket = new UserTicket(user, savedTicket);
		userTicketRepository.save(userTicket);

		// 좌석 플래그 트루로 변경
		scheduleSeat.assign();
		scheduleSeatRepository.save(scheduleSeat);

		return TicketResponse.ticketResponse(savedTicket);

	}

	// 티켓 단건 조회
	@Transactional(readOnly = true)
	public TicketResponse getTicket(AuthUser authUser, Long ticketId) {

		// 유저 검증
		findUser(authUser.getId());

		// 티켓 소유 확인
		UserTicket userTicket = findUserTicketOwner(ticketId, authUser.getId());

		return TicketResponse.ticketResponse(userTicket.getTicket());
	}

	// 티켓 다건 조회
	@Transactional(readOnly = true)
	public Page<TicketResponse> getTickets(
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

		// 티켓 소유 확인
		UserTicket userTicket = findUserTicketOwner(ticketId, authUser.getId());

		// 스케줄 좌석 조회
		ScheduleSeat scheduleSeat = userTicket.getTicket().getScheduleSeat();

		// 좌석 할당 해제
		scheduleSeat.unassign();

		// 티켓 삭제 (소프트 딜리트)
		userTicket.getTicket().cancel();

		// 삭제된 정보 DB에 저장
		ticketRepository.save(userTicket.getTicket());
		scheduleSeatRepository.save(scheduleSeat);
	}

	// 티켓 좌석 변경
	public TicketResponse updateTicket(AuthUser authUser, Long ticketId, TicketChangeRequest requestDto) {
		// 유저 검증
		findUser(authUser.getId());

		// 티켓 소유 확인
		UserTicket userTicket = findUserTicketOwner(ticketId, authUser.getId());

		// 티켓 상태 검증
		if (userTicket.getTicket().getStatus() != TicketStatus.RESERVED) {
			throw new TicketException(TicketErrorCode.TICKET_UPDATE_INVALID_STATUS);
		}

		ScheduleSeat oldScheduleSeat = userTicket.getTicket().getScheduleSeat();
		Long newScheduleSeatId = requestDto.getScheduleSeatId();

		// 좌석 검증
		ScheduleSeat newScheduleSeat = findScheduleSeat(newScheduleSeatId);

		// 기존 좌석 반환
		oldScheduleSeat.unassign();

		// 새 좌석 예약
		newScheduleSeat.assign();

		// 좌석 변경
		userTicket.getTicket().changeScheduleSeat(newScheduleSeat);

		// 기존 좌석 정보 변경, 새 좌석 정보 변경 DB 저장
		scheduleSeatRepository.save(oldScheduleSeat);
		scheduleSeatRepository.save(newScheduleSeat);

		// 티켓 저장
		Ticket updatedTicket = ticketRepository.save(userTicket.getTicket());

		return TicketResponse.ticketResponse(updatedTicket);
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
