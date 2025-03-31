package com.example.concurrencycontrolproject.domain.Ticket.repository;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.concurrencycontrolproject.domain.concert.entity.Concert;
import com.example.concurrencycontrolproject.domain.concert.repository.ConcertRepository;
import com.example.concurrencycontrolproject.domain.schedule.entity.Schedule;
import com.example.concurrencycontrolproject.domain.schedule.enums.ScheduleStatus;
import com.example.concurrencycontrolproject.domain.seat.entity.seat.Seat;
import com.example.concurrencycontrolproject.domain.ticket.dto.response.TicketResponse;
import com.example.concurrencycontrolproject.domain.ticket.entity.Ticket;
import com.example.concurrencycontrolproject.domain.ticket.entity.TicketStatus;
import com.example.concurrencycontrolproject.domain.ticket.repository.TicketRepository;
import com.example.concurrencycontrolproject.domain.user.entity.User;
import com.example.concurrencycontrolproject.domain.userTicket.entity.UserTicket;
import com.example.concurrencycontrolproject.global.config.QueryDslConfig;

import jakarta.persistence.EntityManager;

@DataJpaTest
@Import(QueryDslConfig.class)
public class TicketRepositoryTest {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private TicketRepository ticketRepository;

	@Autowired
	private ConcertRepository concertRepository;

	Logger logger = LoggerFactory.getLogger(TicketRepositoryTest.class);

	// 공용 필드
	private Schedule schedule1, schedule2;
	private Seat seat1, seat2;
	private User user1, user2;
	private Ticket ticket1, ticket2, ticket3; // Test tickets

	// 테스트용 좌석 생성 메서드
	private Seat createSeat(Integer number, String grade, Integer price, String section) {
		return Seat.of(number, grade, price, section);
	}

	@BeforeEach
	void setUp() {

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
		schedule1 = Schedule.of(
			concert,
			LocalDateTime.of(2025, 5, 22, 20, 0),
			ScheduleStatus.ACTIVE);
		entityManager.persist(schedule1);

		schedule2 = Schedule.of(
			concert,
			LocalDateTime.of(2025, 5, 22, 20, 0),
			ScheduleStatus.ACTIVE);
		entityManager.persist(schedule2);

		seat1 = createSeat(1, "A석", 10000, "A구역");
		entityManager.persist(seat1);
		seat2 = createSeat(2, "B석", 8000, "B구역");
		entityManager.persist(seat2);

		user1 = new User();
		entityManager.persist(user1);
		user2 = new User();
		entityManager.persist(user2);

		// 티켓 생성
		ticket1 = Ticket.saveTicket(schedule1.getId(), seat1.getId());
		entityManager.persist(ticket1);
		UserTicket ut1 = new UserTicket(user1, ticket1);
		entityManager.persist(ut1);

		ticket2 = Ticket.saveTicket(schedule1.getId(), seat2.getId());
		entityManager.persist(ticket2);
		UserTicket ut2 = new UserTicket(user1, ticket2);
		entityManager.persist(ut2);

		ticket3 = Ticket.saveTicket(schedule2.getId(), seat1.getId());
		ticket3.expire();
		entityManager.persist(ticket3);
		UserTicket ut3 = new UserTicket(user2, ticket3);
		entityManager.persist(ut3);

		entityManager.flush();
		entityManager.clear();
	}

	@Test
	@DisplayName("ID로 티켓 조회가 가능하다")
	void saveTicketAndFindById() {
		// given

		// when
		Ticket foundTicket = ticketRepository.findById(ticket1.getId()).orElse(null);

		// then
		logger.info("티켓 ID: {}, 스케줄 ID: {}, 시트 ID: {}", foundTicket.getId(),
			foundTicket.getScheduleId(), foundTicket.getSeatId());

		assertThat(foundTicket).isNotNull();
		assertThat(Objects.requireNonNull(foundTicket).getId()).isEqualTo(ticket1.getId());
		assertThat(foundTicket.getStatus()).isEqualTo(TicketStatus.RESERVED);
		assertThat(foundTicket.getScheduleId()).isEqualTo(schedule1.getId());
		assertThat(foundTicket.getSeatId()).isEqualTo(seat1.getId());
	}

	@Test
	@DisplayName("예약된 티켓 존재 시 existsByScheduleIdAndSeatId 가 true 를 반환한다")
	void existsByScheduleIdAndSeatId_whenReserved_returnsTrue() {
		// given

		// when
		boolean exists = ticketRepository.existsByScheduleIdAndSeatIdAndStatusIn(
			schedule1.getId(), seat1.getId(), List.of(TicketStatus.RESERVED)
		);

		// then
		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("예약된 티켓 없으면 existsByScheduleIdAndSeatIdAndStatusIn 가 false 를 반환한다")
	void existsByScheduleIdAndSeatId_whenNotReservedStatus_returnsFalse() {
		// given

		// when
		boolean exists = ticketRepository.existsByScheduleIdAndSeatIdAndStatusIn(
			schedule2.getId(), seat1.getId(), List.of(TicketStatus.RESERVED)
		);

		// then
		assertThat(exists).isFalse();
	}

	@Test
	@DisplayName("해당 좌석 + 스케줄이 없으면 existsByScheduleIdAndSeatIdAndStatusIn 가 false 를 반환한다")
	void existsByScheduleIdAndSeatId_whenSeatOrScheduleNotExist_returnsFalse() {
		// given

		// when
		boolean exists = ticketRepository.existsByScheduleIdAndSeatIdAndStatusIn(
			schedule2.getId(), seat2.getId(), List.of(TicketStatus.RESERVED)
		);

		// then
		assertThat(exists).isFalse();
	}

	@Test
	@DisplayName("특정 사용자가 예매한 티켓 조회을 다건 조회할 수 있다")
	void findTickets_findByUserAndStatus() {
		// given
		Pageable pageable = PageRequest.of(0, 10);

		// when
		Page<TicketResponse> resultPage = ticketRepository.findTickets(
			user1.getId(),
			pageable,
			null,
			"RESERVED",
			null,
			null
		);

		// then
		logger.info("시트1 ID: {}", seat1.getId());
		logger.info("시트2 ID: {}", seat2.getId());

		assertThat(resultPage).isNotNull();
		assertThat(resultPage.getTotalElements()).isEqualTo(2);
		assertThat(resultPage.getContent().size()).isEqualTo(2);

		assertThat(resultPage.getContent().get(0).getStatus()).isEqualTo(TicketStatus.RESERVED);
		assertThat(resultPage.getContent().get(1).getStatus()).isEqualTo(TicketStatus.RESERVED);

		assertThat(resultPage.getContent().get(0).getSeat()).isNotNull();
		assertThat(resultPage.getContent().get(0).getSeat().getId()).isIn(seat1.getId(), seat2.getId());

	}

	@Test
	@DisplayName("특정 사용자의 특정 스케줄의 티켓을 다건 조회할 수 있다")
	void findTickets_findBySchedule() {
		// given
		Pageable pageable = PageRequest.of(0, 10);

		// when
		Page<TicketResponse> resultPage = ticketRepository.findTickets(
			user1.getId(),
			pageable,
			schedule1.getId(),
			null,
			null,
			null
		);

		// then
		logger.info("요소1의 시트 ID: {}", resultPage.getContent().get(0).getSeat().getId());
		logger.info("요소2의 시트 ID: {}", resultPage.getContent().get(1).getSeat().getId());

		assertThat(resultPage).isNotNull();
		assertThat(resultPage.getTotalElements()).isEqualTo(2);
		assertThat(resultPage.getContent().size()).isEqualTo(2);
	}

	@Test
	@DisplayName("특정 사용자의 특정 상태 티켓을 다건 조회할 수 있다")
	void findTickets_findByUserAndCanceledStatus() {
		// given
		Pageable pageable = PageRequest.of(0, 10);

		// when
		Page<TicketResponse> resultPage = ticketRepository.findTickets(
			user2.getId(),
			pageable,
			null,
			"EXPIRED",
			null,
			null
		);

		// then
		logger.info("요소1의 시트 ID: {}", resultPage.getContent().get(0).getSeat().getId());

		assertThat(resultPage).isNotNull();
		assertThat(resultPage.getTotalElements()).isEqualTo(1);
		assertThat(resultPage.getContent().size()).isEqualTo(1);
		assertThat(resultPage.getContent().get(0).getId()).isEqualTo(ticket3.getId());
		assertThat(resultPage.getContent().get(0).getStatus()).isEqualTo(TicketStatus.EXPIRED);
	}

	@Test
	@DisplayName("다건 조회 결과가 없으면 빈페이지를 반환한다")
	void findTickets_noResults() {
		// given
		Pageable pageable = PageRequest.of(0, 10);

		// when
		Page<TicketResponse> resultPage = ticketRepository.findTickets(
			user1.getId(),
			pageable,
			null,
			"EXPIRED",
			null,
			null
		);

		// then
		assertThat(resultPage).isNotNull();
		assertThat(resultPage.getTotalElements()).isEqualTo(0);
		assertThat(resultPage.getContent().isEmpty()).isTrue();
	}
}