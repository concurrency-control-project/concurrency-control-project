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

import com.example.concurrencycontrolproject.domain.canceledticket.entity.CanceledTicket;
import com.example.concurrencycontrolproject.domain.canceledticket.repository.CanceledTicketRepository;
import com.example.concurrencycontrolproject.domain.common.auth.AuthUser;
import com.example.concurrencycontrolproject.domain.schedule.entity.Schedule;
import com.example.concurrencycontrolproject.domain.schedule.enums.ScheduleStatus;
import com.example.concurrencycontrolproject.domain.schedule.repository.ScheduleRepository;
import com.example.concurrencycontrolproject.domain.seat.dto.response.SeatResponseDto;
import com.example.concurrencycontrolproject.domain.seat.entity.seat.Seat;
import com.example.concurrencycontrolproject.domain.seat.repository.seat.SeatRepository;
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
import com.example.concurrencycontrolproject.global.config.aop.DistributedLock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

	private final TicketRepository ticketRepository;
	private final UserRepository userRepository;
	private final UserTicketRepository userTicketRepository;
	private final CanceledTicketRepository canceledTicketRepository;
	private final ScheduleRepository scheduleRepository;
	private final SeatRepository seatRepository;

	// 유저 검증
	private User findUser(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(
				() -> new TicketException(TicketErrorCode.USER_NOT_FOUND));
	}

	// 중복 예매 검증
	private void validateScheduleAndSeat(Long scheduleId, Long seatId) {

		// 스케줄 조회
		scheduleRepository.findById(scheduleId)
			.filter(s -> s.getStatus() == ScheduleStatus.ACTIVE)
			.orElseThrow(() -> new TicketException(TicketErrorCode.SCHEDULE_SEAT_BAD_REQUEST));

		// 좌석 존재 검증
		if (!seatRepository.existsById(seatId)) {
			throw new TicketException(TicketErrorCode.SEAT_NOT_FOUND);
		}

		// 중복 예매 확인
		boolean isUnavailable = ticketRepository.existsByScheduleIdAndSeatIdAndStatusIn(
			scheduleId, seatId, List.of(TicketStatus.RESERVED));
		if (isUnavailable) {
			throw new TicketException(TicketErrorCode.SCHEDULE_SEAT_BAD_REQUEST);
		}
	}

	// 좌석 검증
	private void findSeat(Long seatId) {
		seatRepository.findById(seatId)
			.orElseThrow(
				() -> new TicketException(TicketErrorCode.SEAT_NOT_FOUND));
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

	// seatResponseDto 생성
	private SeatResponseDto findSeatResponseDto(Long seatId) {

		Seat seat = seatRepository.findById(seatId)
			.orElseThrow(
				() -> new TicketException(TicketErrorCode.SEAT_NOT_FOUND));

		return SeatResponseDto.builder()
			.id(seat.getId())
			.number(seat.getNumber())
			.price(seat.getPrice())
			.grade(seat.getGrade())
			.section(seat.getSection())
			.build();
	}

	// 티켓 생성
	@Transactional
	@DistributedLock(keyPrefix = "scheduleIdSeatId", keySuffixExpression = "#scheduleId + '-' + #seatId")
	public TicketResponse saveTicket(AuthUser authUser, Long scheduleId, Long seatId) {

		// 유저 검증
		User user = findUser(authUser.getId());

		// 스케줄 + 좌석 검증
		validateScheduleAndSeat(scheduleId, seatId);

		// Ticket 엔티티 생성 + 저장
		Ticket ticket = Ticket.saveTicket(scheduleId, seatId);
		Ticket savedTicket = ticketRepository.save(ticket);

		// 유저티켓 맵핑
		UserTicket userTicket = new UserTicket(user, savedTicket);
		userTicketRepository.save(userTicket);

		// 시트 정보 생성
		SeatResponseDto seatResponseDto = findSeatResponseDto(seatId);

		return TicketResponse.ticketDetailedResponse(savedTicket, seatResponseDto);
	}

	// 티켓 단건 조회
	@Transactional(readOnly = true)
	public TicketResponse getTicket(AuthUser authUser, Long ticketId) {

		// 유저 검증
		findUser(authUser.getId());

		// 티켓 소유 확인
		UserTicket userTicket = findUserTicketOwner(ticketId, authUser.getId());

		// 티켓 생성
		Ticket ticket = userTicket.getTicket();

		// 좌석 검증
		findSeat(ticket.getSeatId());

		// 시트 정보 생성
		SeatResponseDto seatResponseDto = findSeatResponseDto(ticket.getSeatId());

		return TicketResponse.ticketDetailedResponse(ticket, seatResponseDto);
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
			pageable.getPageNumber() > 0 ? pageable.getPageNumber() - 1 : 0,
			pageable.getPageSize()
		);

		return ticketRepository.findTickets(authUser.getId(), convertPageable, scheduleId, ticketStatus,
			startedAt, endedAt);
	}

	// 티켓 취소
	@Transactional
	public void deleteTicket(AuthUser authUser, Long ticketId) {

		// 유저 검증
		User user = findUser(authUser.getId());

		// 티켓 소유 확인
		UserTicket userTicket = findUserTicketOwner(ticketId, authUser.getId());

		Ticket ticket = userTicket.getTicket();

		// 이미 만료된 티켓인지 확인
		if (ticket.getStatus() != TicketStatus.RESERVED) {
			throw new TicketException(TicketErrorCode.TICKET_BAD_REQUEST);
		}

		// 취소티켓 테이블에 저장
		CanceledTicket canceledTicket = CanceledTicket.canceledTicket(ticket, user);
		canceledTicketRepository.save(canceledTicket);

		// 유저 티켓 맵핑 삭제 (하드딜리트)
		userTicketRepository.delete(userTicket);

		// 티켓 삭제 (하드딜리트)
		ticketRepository.delete(ticket);
	}

	// 티켓 좌석 변경
	@Transactional
	@DistributedLock(keyPrefix = "scheduleIdSeatId", keySuffixExpression = "#scheduleId + '-' + #seatId")
	public TicketResponse updateTicket(AuthUser authUser, Long ticketId, TicketChangeRequest requestDto) {
		// 유저 검증
		findUser(authUser.getId());

		// 티켓 소유 확인
		UserTicket userTicket = findUserTicketOwner(ticketId, authUser.getId());

		// 티켓 생성
		Ticket ticket = userTicket.getTicket();

		// 티켓 상태 검증
		if (ticket.getStatus() != TicketStatus.RESERVED) {
			throw new TicketException(TicketErrorCode.TICKET_UPDATE_INVALID_STATUS);
		}

		// 새 좌석
		Long newSeatId = requestDto.getSeatId();

		// 요청 좌석과 기존 좌석 동일하면 예외 처리
		if (Objects.equals(ticket.getSeatId(), newSeatId)) {
			throw new TicketException(TicketErrorCode.SCHEDULE_SEAT_BAD_REQUEST);
		}

		// 새 좌석 검증
		validateScheduleAndSeat(ticket.getScheduleId(), newSeatId);

		// 좌석 변경
		ticket.changeScheduledSeat(newSeatId);

		// 시트 정보 생성
		SeatResponseDto seatResponseDto = findSeatResponseDto(newSeatId);

		return TicketResponse.ticketDetailedResponse(ticket, seatResponseDto);
	}

	// 티켓을 만료시키는 스케줄링 메서드
	// Todo: 성능 생각하면 굳이 스케줄링으로 하지 말고, 스케줄의 상태 변경 시에 티켓도 전부 변경하는 게 좋아 보입니다.
	@Transactional
	@Scheduled(cron = "0 * * * * *")
	public void expireTicket() {
		List<Ticket> tickets = ticketRepository.findTicketsByStatus(TicketStatus.RESERVED);

		for (Ticket ticket : tickets) {

			try {
				log.info("티켓 상태 갱신 스케줄러 실행");

				Schedule schedule = scheduleRepository.findById(ticket.getScheduleId())
					.orElseThrow(
						() -> new TicketException(TicketErrorCode.SCHEDULE_NOT_FOUND));

				if (schedule.getStatus() != ScheduleStatus.ACTIVE) {
					ticket.expire();
				}
			} catch (Exception e) {
				log.error("티켓 상태 갱신 중 에러 발생", e);
			}

		}
	}

}
