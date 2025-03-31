// package com.example.concurrencycontrolproject.domain.Ticket.service;
//
// import static com.example.concurrencycontrolproject.domain.seat.entity.seat.Seat.*;
// import static org.assertj.core.api.AssertionsForClassTypes.*;
// import static org.junit.jupiter.api.Assertions.*;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.test.util.ReflectionTestUtils;
// import org.springframework.transaction.annotation.Transactional;
//
// import com.example.concurrencycontrolproject.domain.common.auth.AuthUser;
// import com.example.concurrencycontrolproject.domain.schedule.entity.Schedule;
// import com.example.concurrencycontrolproject.domain.scheduleSeat.entity.ScheduleSeat;
// import com.example.concurrencycontrolproject.domain.scheduleSeat.response.ScheduleSeatRepository;
// import com.example.concurrencycontrolproject.domain.seat.entity.seat.Seat;
// import com.example.concurrencycontrolproject.domain.ticket.dto.request.TicketChangeRequest;
// import com.example.concurrencycontrolproject.domain.ticket.dto.response.TicketResponse;
// import com.example.concurrencycontrolproject.domain.ticket.entity.Ticket;
// import com.example.concurrencycontrolproject.domain.ticket.entity.TicketStatus;
// import com.example.concurrencycontrolproject.domain.ticket.exception.TicketException;
// import com.example.concurrencycontrolproject.domain.ticket.repository.TicketRepository;
// import com.example.concurrencycontrolproject.domain.ticket.service.TicketService;
// import com.example.concurrencycontrolproject.domain.user.entity.User;
// import com.example.concurrencycontrolproject.domain.user.enums.UserRole;
// import com.example.concurrencycontrolproject.domain.user.repository.UserRepository;
// import com.example.concurrencycontrolproject.domain.userTicket.entity.UserTicket;
// import com.example.concurrencycontrolproject.domain.userTicket.repository.UserTicketRepository;
//
// import jakarta.persistence.EntityManager;
//
// @SpringBootTest
// @Transactional
// public class TicketServiceTest {
//
// 	@Autowired
// 	private TicketService ticketService;
//
// 	@Autowired
// 	private TicketRepository ticketRepository;
//
// 	@Autowired
// 	private UserRepository userRepository;
//
// 	@Autowired
// 	private ScheduleSeatRepository scheduleSeatRepository;
//
// 	@Autowired
// 	private EntityManager entityManager;
//
// 	@Autowired
// 	private UserTicketRepository userTicketRepository;
//
// 	Logger logger = LoggerFactory.getLogger(TicketService.class);
//
// 	// 공용 필드
// 	private User user;
// 	private AuthUser authUser;
// 	private Schedule schedule;
// 	private Seat seat1, seat2;
// 	private ScheduleSeat scheduleSeat1, scheduleSeat2;
//
// 	// 테스트용 좌석 생성 메서드
// 	private Seat createSeat(Integer number, String grade, Integer price, String section) {
//
// 		Seat seat = of(1, "A석", 10000, "A열");
// 		ReflectionTestUtils.setField(seat, "number", number);
// 		ReflectionTestUtils.setField(seat, "grade", grade);
// 		ReflectionTestUtils.setField(seat, "price", price);
// 		ReflectionTestUtils.setField(seat, "section", section);
//
// 		return seat;
// 	}
//
// 	// 테스트용 스케줄시트 생성 메서드
// 	private ScheduleSeat createScheduleSeat(Schedule schedule, Seat seat) {
//
// 		ScheduleSeat scheduleSeat = new ScheduleSeat();
// 		ReflectionTestUtils.setField(scheduleSeat, "schedule", schedule);
// 		ReflectionTestUtils.setField(scheduleSeat, "seat", seat);
//
// 		return scheduleSeat;
// 	}
//
// 	@BeforeEach
// 	void setUp() {
//
// 		// 테스트 전, 컨텍스트 초기화 (명시적)
// 		entityManager.clear();
//
// 		// 유저 생성 및 저장
// 		user = new User();
// 		userRepository.saveAndFlush(user); // 바로 DB로 저장
//
// 		// authUser 생성
// 		authUser = new AuthUser(user.getId(), "유저이메일", UserRole.ROLE_USER, "유저닉네임");
//
// 		// 스케줄 생성 및 저장
// 		schedule = new Schedule();
// 		entityManager.persist(schedule);
//
// 		// 좌석 생성 및 저장
// 		seat1 = createSeat(1, "A석", 10000, "A구역");
// 		entityManager.persist(seat1);
// 		seat2 = createSeat(2, "B석", 8000, "B구역");
// 		entityManager.persist(seat2);
//
// 		// 스케줄시트 생성 및 저장
// 		scheduleSeat1 = createScheduleSeat(schedule, seat1);
// 		scheduleSeatRepository.save(scheduleSeat1);
// 		scheduleSeat2 = createScheduleSeat(schedule, seat2);
// 		scheduleSeatRepository.save(scheduleSeat2);
//
// 		// DB 저장
// 		entityManager.flush();
//
// 		// 컨텍스트 초기화
// 		entityManager.clear();
// 	}
//
// 	@Test
// 	@DisplayName("티켓 생성에 성공한다")
// 	void saveTicket_success() {
// 		// given
//
// 		// when
// 		TicketResponse responseDto = ticketService.saveTicket(authUser, scheduleSeat1.getId());
// 		ScheduleSeat reservedSeat = scheduleSeatRepository.findById(scheduleSeat1.getId()).orElseThrow();
//
// 		// then
// 		assertThat(responseDto).isNotNull();
// 		assertThat(responseDto.getSeat().getId()).isEqualTo(seat1.getId());
// 		assertThat(responseDto.getStatus()).isEqualTo(TicketStatus.RESERVED);
// 		assertThat(reservedSeat.isAssigned()).isTrue();
// 	}
//
// 	@Test
// 	@DisplayName("이미 예약된 좌석으로 티켓 생성 시 예외가 발생한다")
// 	void saveTicket_whenSeatAlreadyAssigned_throwsException() {
// 		// given
// 		scheduleSeat1.assign(); // 미리 예약 상태로 설정
// 		scheduleSeatRepository.saveAndFlush(scheduleSeat1);
// 		entityManager.clear();
//
// 		Long alreadyReservedSeatId = scheduleSeat1.getId();
//
// 		// when, then
// 		TicketException exception = assertThrows(TicketException.class,
// 			() -> ticketService.saveTicket(authUser, alreadyReservedSeatId));
//
// 		assertThat(exception.getMessage()).isEqualTo("올바르지 않거나 선택할 수 없는 좌석입니다.");
// 	}
//
// 	@Test
// 	@DisplayName("티켓 좌석 변경에 성공한다")
// 	void updateTicket_success() {
//
// 		// given
// 		Ticket ticket = Ticket.saveTicket(scheduleSeat1); // 티켓생성
// 		ticketRepository.save(ticket);
//
// 		scheduleSeat1.assign(); // 좌석 할당
// 		scheduleSeatRepository.save(scheduleSeat1);
//
// 		UserTicket userTicket = new UserTicket(user, ticket);
// 		userTicketRepository.save(userTicket);
//
// 		entityManager.flush();
// 		entityManager.clear();
//
// 		Long ticketId = ticket.getId();
// 		Long newScheduleSeatId = scheduleSeat2.getId();
//
// 		TicketChangeRequest requestDto = new TicketChangeRequest(newScheduleSeatId); // 변경 요청 DTO 생성
//
// 		// when, then
// 		TicketResponse responseDto = ticketService.updateTicket(authUser, ticketId, requestDto);
// 		assertThat(responseDto).isNotNull();
// 		assertThat(responseDto.getId()).isEqualTo(ticketId);
// 		assertThat(responseDto.getSeat().getId()).isEqualTo(seat2.getId()); // 좌석 변경 확인
//
// 		Ticket updatedTicket = ticketRepository.findById(ticketId).orElseThrow();
// 		assertThat(updatedTicket.getScheduleSeat().getId()).isEqualTo(newScheduleSeatId); // 변경 된 좌석 DB 확인
//
// 		ScheduleSeat oldSeat = scheduleSeatRepository.findById(scheduleSeat1.getId()).orElseThrow();
// 		assertThat(oldSeat.isAssigned()).isFalse(); // 기존 좌석 반환 확인
//
// 		ScheduleSeat newSeat = scheduleSeatRepository.findById(newScheduleSeatId).orElseThrow();
// 		assertThat(newSeat.isAssigned()).isTrue(); // 새 좌석 할당 확인
// 	}
//
// 	@Test
// 	@DisplayName("존재하지 않는 티켓으로 좌석 변경 시 예외가 발생한다")
// 	void updateTicket_whenTicketNotFound_throwsException() {
// 		// given
// 		Long nonExistingTicketId = 1000L;
// 		Long newScheduleSeatId = scheduleSeat2.getId();
// 		TicketChangeRequest requestDto = new TicketChangeRequest(newScheduleSeatId);
//
// 		// when, then
// 		TicketException exception = assertThrows(TicketException.class,
// 			() -> ticketService.updateTicket(authUser, nonExistingTicketId, requestDto));
//
// 		assertThat(exception.getMessage()).isEqualTo("티켓을 찾을 수 없습니다.");
// 	}
//
// 	@Test
// 	@DisplayName("변경하려는 좌석이 이미 예약된 경우 예외가 발생한다")
// 	void updateTicket_whenNewSeatIsAssigned_throwsException() {
// 		// given
// 		Ticket ticket = Ticket.saveTicket(scheduleSeat1);
// 		ticketRepository.save(ticket);
//
// 		scheduleSeat1.assign();
// 		scheduleSeatRepository.save(scheduleSeat1);
//
// 		scheduleSeat2.assign(); // 변경 하려는 좌석도 예약 상태 설정
// 		scheduleSeatRepository.save(scheduleSeat2);
//
// 		UserTicket userTicket = new UserTicket(user, ticket);
// 		userTicketRepository.save(userTicket);
//
// 		entityManager.flush();
// 		entityManager.clear();
//
// 		Long ticketId = ticket.getId();
// 		Long assignedNewSeatId = scheduleSeat2.getId();
// 		TicketChangeRequest requestDto = new TicketChangeRequest(assignedNewSeatId);
//
// 		// when, then
// 		TicketException exception = assertThrows(TicketException.class,
// 			() -> ticketService.updateTicket(authUser, ticketId, requestDto));
//
// 		assertThat(exception.getMessage()).isEqualTo("올바르지 않거나 선택할 수 없는 좌석입니다.");
// 	}
//
// 	@Test
// 	@DisplayName("티켓 단건 조회에 성공한다")
// 	void getTicket_success() {
// 		// given
// 		Ticket ticket = Ticket.saveTicket(scheduleSeat1);
// 		ticketRepository.save(ticket);
//
// 		UserTicket userTicket = new UserTicket(user, ticket);
// 		userTicketRepository.save(userTicket);
//
// 		entityManager.clear();
//
// 		Long ticketId = ticket.getId();
//
// 		// when
// 		TicketResponse responseDto = ticketService.getTicket(authUser, ticketId);
//
// 		logger.info("로그1 = {}", ticket.getId());
// 		logger.info("로그2 = {}", ticket.getScheduleSeat().getId());
// 		logger.info("로그3 = {}", ticket.getScheduleSeat().getSeat().getPrice());
//
// 		// then
// 		assertThat(responseDto).isNotNull();
// 		assertThat(responseDto.getId()).isEqualTo(ticketId);
// 		assertThat(responseDto.getStatus()).isEqualTo(TicketStatus.RESERVED);
// 		assertThat(responseDto.getSeat().getId()).isEqualTo(seat1.getId());
// 		assertThat(responseDto.getScheduleId()).isEqualTo(schedule.getId());
// 	}
//
// 	@Test
// 	@DisplayName("존재하지 않는 티켓 단건 조회 시 예외가 발생한다")
// 	void getTicket_whenTicketNotFound_throwsException() {
// 		// given
// 		Long nonExistingTicketId = 1000L;
//
// 		// when, then
// 		TicketException exception = assertThrows(TicketException.class,
// 			() -> ticketService.getTicket(authUser, nonExistingTicketId));
//
// 		assertThat(exception.getMessage()).isEqualTo("티켓을 찾을 수 없습니다.");
// 	}
//
// 	@Test
// 	@DisplayName("자신이 소유하지 않은 티켓 단건 조회 시 예외가 발생한다")
// 	void getTicket_whenNotOwner_throwsException() {
// 		// given
//
// 		// 티켓1
// 		Ticket ticket1 = Ticket.saveTicket(scheduleSeat1);
// 		ticketRepository.save(ticket1);
//
// 		UserTicket userTicket = new UserTicket(user, ticket1);
// 		userTicketRepository.save(userTicket);
//
// 		Long ticket1Id = ticket1.getId();
//
// 		// 다른 유저
// 		User otherUser = new User();
// 		userRepository.save(otherUser);
//
// 		AuthUser otherAuthUser = new AuthUser(otherUser.getId(), "다른 유저 이메일", UserRole.ROLE_USER, "다른유저");
//
// 		entityManager.flush();
// 		entityManager.clear();
//
// 		// when, then
// 		TicketException exception = assertThrows(TicketException.class,
// 			() -> ticketService.getTicket(otherAuthUser, ticket1Id));
// 		assertThat(exception.getMessage()).isEqualTo("구매하신 티켓이 아닙니다.");
// 	}
//
// 	@Test
// 	@DisplayName("티켓 다건 조회에 성공한다")
// 	void getTickets_success() {
// 		// given
// 		Ticket ticket1 = Ticket.saveTicket(scheduleSeat1);
// 		ticket1.cancel();
// 		ticketRepository.save(ticket1);
//
// 		UserTicket userTicket = new UserTicket(user, ticket1);
// 		userTicketRepository.save(userTicket);
//
// 		Ticket ticket2 = Ticket.saveTicket(scheduleSeat2);
// 		ticketRepository.save(ticket2);
//
// 		UserTicket userTicket2 = new UserTicket(user, ticket2);
// 		userTicketRepository.save(userTicket2);
//
// 		entityManager.flush();
// 		entityManager.clear();
//
// 		Pageable pageable = PageRequest.of(1, 10);
//
// 		// when
// 		Page<TicketResponse> reservedPage = ticketService.getTickets(authUser, pageable, null, "RESERVED", null, null);
// 		Page<TicketResponse> canceledPage = ticketService.getTickets(authUser, pageable, null, "CANCELED", null, null);
//
// 		// then
// 		// RESERVED
// 		assertThat(reservedPage).isNotNull();
// 		assertThat(reservedPage.getTotalElements()).isEqualTo(1);
// 		assertThat(reservedPage.getContent().size()).isEqualTo(1);
// 		assertThat(reservedPage.getContent().get(0).getId()).isEqualTo(ticket2.getId());
// 		assertThat(reservedPage.getContent().get(0).getStatus()).isEqualTo(TicketStatus.RESERVED);
//
// 		// CANCELED
// 		assertThat(canceledPage).isNotNull();
// 		assertThat(canceledPage.getTotalElements()).isEqualTo(1);
// 		assertThat(canceledPage.getContent().size()).isEqualTo(1);
// 		assertThat(canceledPage.getContent().get(0).getId()).isEqualTo(ticket1.getId());
// 		assertThat(canceledPage.getContent().get(0).getStatus()).isEqualTo(TicketStatus.CANCELED);
// 	}
//
// 	@Test
// 	@DisplayName("티켓 취소에 성공한다")
// 	void deleteTicket_success() {
// 		// given
// 		Ticket ticket = Ticket.saveTicket(scheduleSeat1);
// 		ticketRepository.save(ticket);
//
// 		UserTicket userTicket = new UserTicket(user, ticket);
// 		userTicketRepository.save(userTicket);
//
// 		scheduleSeat1.assign();
// 		scheduleSeatRepository.save(scheduleSeat1);
//
// 		entityManager.flush();
// 		entityManager.clear();
//
// 		Long ticketId = ticket.getId();
//
// 		// when
// 		ticketService.deleteTicket(authUser, ticketId);
// 		entityManager.flush();
// 		entityManager.clear();
//
// 		// then
// 		Ticket canceledTicket = ticketRepository.findById(ticketId).orElseThrow();
// 		assertThat(canceledTicket.getStatus()).isEqualTo(TicketStatus.CANCELED);
//
// 		ScheduleSeat relatedSeat = scheduleSeatRepository.findById(scheduleSeat1.getId()).orElseThrow();
// 		assertThat(relatedSeat.isAssigned()).isFalse();
// 	}
//
// }
