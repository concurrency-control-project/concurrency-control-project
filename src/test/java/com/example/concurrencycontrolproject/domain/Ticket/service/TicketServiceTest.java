package com.example.concurrencycontrolproject.domain.Ticket.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.example.concurrencycontrolproject.domain.canceledticket.repository.CanceledTicketRepository;
import com.example.concurrencycontrolproject.domain.common.auth.AuthUser;
import com.example.concurrencycontrolproject.domain.concert.entity.Concert;
import com.example.concurrencycontrolproject.domain.concert.repository.ConcertRepository;
import com.example.concurrencycontrolproject.domain.schedule.entity.Schedule;
import com.example.concurrencycontrolproject.domain.schedule.enums.ScheduleStatus;
import com.example.concurrencycontrolproject.domain.schedule.repository.ScheduleRepository;
import com.example.concurrencycontrolproject.domain.seat.entity.seat.Seat;
import com.example.concurrencycontrolproject.domain.seat.repository.seat.SeatRepository;
import com.example.concurrencycontrolproject.domain.ticket.dto.request.TicketChangeRequest;
import com.example.concurrencycontrolproject.domain.ticket.dto.response.TicketResponse;
import com.example.concurrencycontrolproject.domain.ticket.entity.Ticket;
import com.example.concurrencycontrolproject.domain.ticket.entity.TicketStatus;
import com.example.concurrencycontrolproject.domain.ticket.exception.TicketErrorCode;
import com.example.concurrencycontrolproject.domain.ticket.exception.TicketException;
import com.example.concurrencycontrolproject.domain.ticket.repository.TicketRepository;
import com.example.concurrencycontrolproject.domain.ticket.service.TicketService;
import com.example.concurrencycontrolproject.domain.user.entity.User;
import com.example.concurrencycontrolproject.domain.user.enums.UserRole;
import com.example.concurrencycontrolproject.domain.user.repository.UserRepository;
import com.example.concurrencycontrolproject.domain.userTicket.repository.UserTicketRepository;

import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional
public class TicketServiceTest {

	@Autowired
	private TicketService ticketService;

	@Autowired
	private TicketRepository ticketRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ScheduleRepository scheduleRepository;

	@Autowired
	private SeatRepository seatRepository;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private ConcertRepository concertRepository;

	@Autowired
	private CanceledTicketRepository canceledTicketRepository;

	@Autowired
	private UserTicketRepository userTicketRepository;

	Logger logger = LoggerFactory.getLogger(TicketServiceTest.class);

	// 공용 필드
	private User user1, user2;
	private AuthUser authUser1, authUser2;
	private Schedule schedule1, schedule2;
	private Seat seat1, seat2, seat3;

	private Seat createAndSaveSeat(Integer number, String grade, Integer price, String section) {
		Seat seat = Seat.of(number, grade, price, section);
		return seatRepository.save(seat);
	}

	@BeforeEach
	void setUp() {

		// 유저 생성 및 저장
		user1 = new User();
		userRepository.saveAndFlush(user1);
		user2 = new User();
		userRepository.saveAndFlush(user2);

		// authUser 생성
		authUser1 = new AuthUser(user1.getId(), "user1@email.com", UserRole.ROLE_USER, "유저1닉네임");
		authUser2 = new AuthUser(user2.getId(), "user2@email.com", UserRole.ROLE_USER, "유저2닉네임");

		Concert concert = concertRepository.save(
			Concert.builder()
				.title("Spring Festival")
				.performer("Artist")
				.description("Awesome spring concert")
				.concertStartDateTime(LocalDateTime.of(2025, 5, 1, 18, 0))
				.concertEndDateTime(LocalDateTime.of(2025, 5, 31, 22, 0))
				.bookingStartDateTime(LocalDateTime.of(2025, 4, 1, 0, 0))
				.bookingEndDateTime(LocalDateTime.of(2025, 4, 30, 23, 59))
				.location("Seoul")
				.build()
		);

		// 스케줄 생성 및 저장
		schedule1 = scheduleRepository.save(
			Schedule.of(
				concert,
				LocalDateTime.of(2025, 5, 22, 20, 0),
				ScheduleStatus.ACTIVE));

		schedule2 = scheduleRepository.save(
			Schedule.of(
				concert,
				LocalDateTime.of(2025, 5, 22, 20, 0),
				ScheduleStatus.ACTIVE));

		// 좌석 생성 및 저장
		seat1 = createAndSaveSeat(1, "A석", 100000, "A열");
		seat2 = createAndSaveSeat(2, "B석", 80000, "B열");
		seat3 = createAndSaveSeat(3, "C석", 50000, "C열");

		// entityManager.flush(); // @Transactional 사용시, flush + clear 불필요
		// entityManager.clear();
	}

	@Test
	@DisplayName("티켓 생성에 성공한다")
	void saveTicket_success() {
		// given
		Long scheduleId = schedule1.getId();
		Long seatId = seat1.getId();

		// when
		TicketResponse responseDto = ticketService.saveTicket(authUser1, scheduleId, seatId);

		// then
		assertThat(responseDto).isNotNull();
		assertThat(responseDto.getScheduleId()).isEqualTo(scheduleId);
		assertThat(responseDto.getSeat().getId()).isEqualTo(seatId);
		assertThat(responseDto.getStatus()).isEqualTo(TicketStatus.RESERVED);
	}

	@Test
	@DisplayName("이미 예약된 좌석으로 티켓 생성 시 예외가 발생한다")
	void saveTicket_whenSeatAlreadyAssigned_throwsException() {
		// given
		TicketResponse ticketResponse = ticketService.saveTicket(authUser1, schedule1.getId(), seat1.getId());

		// when, then
		logger.info("티켓 상태: {}", ticketResponse.getStatus());

		TicketException exception = assertThrows(TicketException.class,
			() -> ticketService.saveTicket(authUser2, schedule1.getId(), seat1.getId()));

		assertThat(exception.getErrorCode()).isEqualTo(TicketErrorCode.SCHEDULE_SEAT_BAD_REQUEST);
	}

	@Test
	@DisplayName("공연이 끝난 스케줄의 좌석으로 티켓 생성 시 예외가 발생한다")
	void saveTicket_whenScheduleIsDeleted_throwsException() {
		// given
		Concert concert = concertRepository.save(
			Concert.builder()
				.title("Spring Festival")
				.performer("Artist")
				.description("Awesome spring concert")
				.concertStartDateTime(LocalDateTime.of(2025, 5, 1, 18, 0))
				.concertEndDateTime(LocalDateTime.of(2025, 5, 31, 22, 0))
				.bookingStartDateTime(LocalDateTime.of(2025, 4, 1, 0, 0))
				.bookingEndDateTime(LocalDateTime.of(2025, 4, 30, 23, 59))
				.location("Seoul")
				.build()
		);

		Schedule deletedSchedule = scheduleRepository.save(
			Schedule.of(
				concert,
				LocalDateTime.of(2025, 5, 22, 20, 0),
				ScheduleStatus.DELETED));
		Long scheduleId = deletedSchedule.getId();
		Long seatId = seat1.getId();

		// when, then
		logger.info("스케줄 상태: {}", deletedSchedule.getStatus());

		TicketException exception = assertThrows(TicketException.class,
			() -> ticketService.saveTicket(authUser1, scheduleId, seatId));

		assertThat(exception.getErrorCode()).isEqualTo(TicketErrorCode.SCHEDULE_SEAT_BAD_REQUEST);
	}

	@Test
	@DisplayName("티켓 좌석 변경에 성공한다")
	void updateTicket_success() {
		// given
		TicketResponse initialTicketResponse = ticketService.saveTicket(authUser1, schedule1.getId(), seat1.getId());
		Long ticketId = initialTicketResponse.getId();

		Long newSeatId = seat2.getId();
		TicketChangeRequest ticketChangeRequest = new TicketChangeRequest(newSeatId);

		// when
		TicketResponse updatedResponse = ticketService.updateTicket(authUser1, ticketId, ticketChangeRequest);

		// then
		assertThat(updatedResponse).isNotNull();
		assertThat(updatedResponse.getId()).isEqualTo(ticketId);
		assertThat(updatedResponse.getSeat().getId()).isEqualTo(newSeatId);

		// DB 확인
		Ticket updatedTicket = ticketRepository.findById(ticketId).orElseThrow();
		assertThat(updatedTicket.getSeatId()).isEqualTo(newSeatId);

		// 이전 좌석 예약 가능한 지 확인
		boolean oldSeatTaken = ticketRepository.existsByScheduleIdAndSeatIdAndStatusIn(schedule1.getId(), seat1.getId(),
			List.of(TicketStatus.RESERVED));
		assertThat(oldSeatTaken).isFalse();

		// 현재 좌석 예약 불가능한 지 확인
		boolean newSeatTaken = ticketRepository.existsByScheduleIdAndSeatIdAndStatusIn(schedule1.getId(), newSeatId,
			List.of(TicketStatus.RESERVED));
		assertThat(newSeatTaken).isTrue();
	}

	@Test
	@DisplayName("존재하지 않는 티켓으로 좌석 변경 시 예외가 발생한다")
	void updateTicket_whenTicketNotFound_throwsException() {
		// given
		Long nonExistingTicketId = 9999L;
		TicketChangeRequest requestDto = new TicketChangeRequest(seat1.getId());

		// when, then
		TicketException exception = assertThrows(TicketException.class,
			() -> ticketService.updateTicket(authUser1, nonExistingTicketId, requestDto));
		assertThat(exception.getErrorCode()).isEqualTo(TicketErrorCode.TICKET_NOT_FOUND);
	}

	@Test
	@DisplayName("변경하려는 좌석이 이미 예약된 경우 예외가 발생한다")
	void updateTicket_whenNewSeatIsAssigned_throwsException() {
		// given
		TicketResponse ticket1Response = ticketService.saveTicket(authUser1, schedule1.getId(), seat1.getId());
		Long ticket1Id = ticket1Response.getId();

		ticketService.saveTicket(authUser2, schedule1.getId(), seat2.getId());

		TicketChangeRequest ticketChangeRequest = new TicketChangeRequest(seat2.getId());

		// when, then
		TicketException exception = assertThrows(TicketException.class,
			() -> ticketService.updateTicket(authUser1, ticket1Id, ticketChangeRequest));

		assertThat(exception.getErrorCode()).isEqualTo(TicketErrorCode.SCHEDULE_SEAT_BAD_REQUEST);
	}

	@Test
	@DisplayName("티켓 단건 조회에 성공한다")
	void getTicket_success() {
		// given
		TicketResponse savedTicketDto = ticketService.saveTicket(authUser1, schedule1.getId(), seat1.getId());
		Long ticketId = savedTicketDto.getId();

		// when
		TicketResponse responseDto = ticketService.getTicket(authUser1, ticketId);

		// then
		assertThat(responseDto).isNotNull();
		assertThat(responseDto.getId()).isEqualTo(ticketId);
		assertThat(responseDto.getStatus()).isEqualTo(TicketStatus.RESERVED);
		assertThat(responseDto.getSeat().getId()).isEqualTo(seat1.getId());
		assertThat(responseDto.getScheduleId()).isEqualTo(schedule1.getId());
	}

	@Test
	@DisplayName("존재하지 않는 티켓 단건 조회 시 예외가 발생한다")
	void getTicket_whenTicketNotFound_throwsException() {
		// given
		Long nonExistingTicketId = 9999L;

		// when, then
		TicketException exception = assertThrows(TicketException.class,
			() -> ticketService.getTicket(authUser1, nonExistingTicketId));

		assertThat(exception.getErrorCode()).isEqualTo(TicketErrorCode.TICKET_NOT_FOUND);
	}

	@Test
	@DisplayName("자신이 소유하지 않은 티켓 단건 조회 시 예외가 발생한다")
	void getTicket_whenNotOwner_throwsException() {
		// given
		TicketResponse user1TicketResponse = ticketService.saveTicket(authUser1, schedule1.getId(), seat1.getId());
		Long user1TicketId = user1TicketResponse.getId();

		// when, then
		TicketException exception = assertThrows(TicketException.class,
			() -> ticketService.getTicket(authUser2, user1TicketId));
		assertThat(exception.getErrorCode()).isEqualTo(TicketErrorCode.TICKET_ACCESS_DENIED);
	}

	@Test
	@DisplayName("상태 값으로 티켓 다건 조회에 성공한다")
	void getTickets_successWithStatusFilter() {
		// given
		TicketResponse ticket1 = ticketService.saveTicket(authUser1, schedule1.getId(), seat1.getId());
		TicketResponse ticket2 = ticketService.saveTicket(authUser1, schedule1.getId(), seat2.getId());
		ticketService.deleteTicket(authUser1, ticket2.getId());
		TicketResponse ticket3 = ticketService.saveTicket(authUser1, schedule2.getId(), seat3.getId());

		Pageable pageable = PageRequest.of(1, 10);

		// when
		Page<TicketResponse> reservedPage = ticketService.getTickets(
			authUser1,
			pageable,
			null,
			"RESERVED",
			null,
			null
		);

		// then
		// RESERVED
		assertThat(reservedPage).isNotNull();
		assertThat(reservedPage.getTotalElements()).isEqualTo(2);
		assertThat(reservedPage.getContent().size()).isEqualTo(2);
		assertThat(reservedPage.getContent().get(0).getStatus()).isEqualTo(TicketStatus.RESERVED);

		List<Long> reservedIds = reservedPage.getContent().stream().map(TicketResponse::getId).toList();
		assertThat(reservedIds).containsExactlyInAnyOrder(ticket1.getId(), ticket3.getId());
	}

	@Test
	@DisplayName("스케줄 값으로 티켓 다건 조회에 성공한다")
	void getTickets_successWithScheduleFilter() {
		// given
		TicketResponse t1 = ticketService.saveTicket(authUser1, schedule1.getId(), seat1.getId());
		TicketResponse t2 = ticketService.saveTicket(authUser1, schedule1.getId(), seat2.getId());

		Pageable pageable = PageRequest.of(1, 10);

		// when
		Page<TicketResponse> schedule1Page = ticketService.getTickets(authUser1, pageable, schedule1.getId(), null,
			null, null);

		// then
		assertThat(schedule1Page).isNotNull();
		assertThat(schedule1Page.getTotalElements()).isEqualTo(2);
		assertThat(schedule1Page.getContent()).hasSize(2);
		List<Long> schedule1Ids = schedule1Page.getContent().stream().map(TicketResponse::getId).toList();
		assertThat(schedule1Ids).containsExactlyInAnyOrder(t1.getId(), t2.getId());
	}

	@Test
	@DisplayName("티켓 취소에 성공한다")
	void deleteTicket_success() {
		// given
		TicketResponse savedTicketDto = ticketService.saveTicket(authUser1, schedule1.getId(), seat1.getId());
		Long ticketId = savedTicketDto.getId();
		Long scheduleId = savedTicketDto.getScheduleId();
		Long seatId = savedTicketDto.getSeat().getId();

		// when
		ticketService.deleteTicket(authUser1, ticketId);

		// then
		// 원본 티켓 삭제 확인
		boolean ticketExists = ticketRepository.existsById(ticketId);
		assertThat(ticketExists).isFalse();

		// 유저티켓 매핑 삭제 확인
		boolean userTicketExists = userTicketRepository.existsByTicketId(ticketId);
		assertThat(userTicketExists).isFalse();

		// 취소된 티켓 생성 확인
		long canceledTicketCount = canceledTicketRepository.count();
		assertThat(canceledTicketCount).isEqualTo(1);

		// 원본 티켓의 좌석 재예매 가능 여부 확인
		boolean seatStillReserved = ticketRepository.existsByScheduleIdAndSeatIdAndStatusIn(scheduleId,
			seatId, List.of(TicketStatus.RESERVED));
		assertThat(seatStillReserved).isFalse();
	}

	@Test
	@DisplayName("EXPIRED 상태의 티켓 취소 시 예외가 발생한다")
	void deleteTicket_whenStatusIsExpired_throwsException() {
		// given
		TicketResponse ticketResponse = ticketService.saveTicket(authUser1, schedule1.getId(), seat1.getId());
		Long ticketId = ticketResponse.getId();
		Ticket ticket = ticketRepository.findById(ticketId).orElseThrow();
		ticket.expire();
		ticketRepository.save(ticket);

		// when, then
		TicketException exception = assertThrows(TicketException.class,
			() -> ticketService.deleteTicket(authUser1, ticketId));

		assertThat(exception.getErrorCode()).isEqualTo(TicketErrorCode.TICKET_BAD_REQUEST);
	}

	@Test
	@DisplayName("취소된 좌석을 다른 사용자가 다시 예매할 수 있다")
	void saveTicket_whenSeatOfDeletedTicket_success() {
		// given
		Long scheduleId = schedule1.getId();
		Long seatId = seat1.getId();
		TicketResponse savedTicketDto = ticketService.saveTicket(authUser1, scheduleId, seatId);
		Long originalTicketId = savedTicketDto.getId();
		ticketService.deleteTicket(authUser1, originalTicketId);

		// when
		TicketResponse responseDto = ticketService.saveTicket(authUser2, scheduleId, seatId);

		// then
		assertThat(responseDto).isNotNull();
		assertThat(responseDto.getScheduleId()).isEqualTo(scheduleId);
		assertThat(responseDto.getSeat().getId()).isEqualTo(seatId);
		assertThat(responseDto.getStatus()).isEqualTo(TicketStatus.RESERVED);

		// DB 확인
		// 원래 티켓은 여전히 존재 X
		assertThat(ticketRepository.findById(originalTicketId)).isEmpty();

		// 새로 생성된 티켓이 존재하고 RESERVED 상태
		Ticket newTicket = ticketRepository.findById(responseDto.getId()).orElseThrow();
		assertThat(newTicket.getStatus()).isEqualTo(TicketStatus.RESERVED);
		assertThat(newTicket.getScheduleId()).isEqualTo(scheduleId);
		assertThat(newTicket.getSeatId()).isEqualTo(seatId);

		// 해당 좌석은 이제 새 티켓으로 인해 예약된 상태
		boolean seatIsNowReserved = ticketRepository.existsByScheduleIdAndSeatIdAndStatusIn(scheduleId,
			seatId, List.of(TicketStatus.RESERVED));
		assertThat(seatIsNowReserved).isTrue();

		// 유저티켓 매핑 확인
		boolean newUserTicketExists = userTicketRepository.existsByUserAndTicket(user2, newTicket);
		assertThat(newUserTicketExists).isTrue();
	}

}