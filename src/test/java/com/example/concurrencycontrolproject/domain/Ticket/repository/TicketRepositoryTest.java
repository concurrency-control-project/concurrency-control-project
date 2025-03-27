package com.example.concurrencycontrolproject.domain.Ticket.repository;

import static com.example.concurrencycontrolproject.domain.seat.entity.seat.Seat.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.concurrencycontrolproject.config.QueryDslConfig;
import com.example.concurrencycontrolproject.domain.schedule.entity.Schedule;
import com.example.concurrencycontrolproject.domain.scheduleSeat.entity.ScheduleSeat;
import com.example.concurrencycontrolproject.domain.seat.entity.seat.Seat;
import com.example.concurrencycontrolproject.domain.ticket.entity.Ticket;
import com.example.concurrencycontrolproject.domain.ticket.entity.TicketStatus;
import com.example.concurrencycontrolproject.domain.ticket.repository.TicketRepository;
import com.example.concurrencycontrolproject.domain.ticket.service.TicketService;

@DataJpaTest
@Import(QueryDslConfig.class)
public class TicketRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private TicketRepository ticketRepository;

	Logger logger = LoggerFactory.getLogger(TicketService.class);

	// 공용 필드
	private Schedule schedule;
	private Seat seat;
	private ScheduleSeat scheduleSeat;
	private Ticket ticket;

	// 테스트용 좌석 생성 메서드
	private Seat createSeat(Integer number, String grade, Integer price, String section) {

		Seat seat = of(1, "A석", 10000, "A열");
		ReflectionTestUtils.setField(seat, "number", number);
		ReflectionTestUtils.setField(seat, "grade", grade);
		ReflectionTestUtils.setField(seat, "price", price);
		ReflectionTestUtils.setField(seat, "section", section);

		return seat;
	}

	// 테스트용 스케줄시트 생성 메서드
	private ScheduleSeat createScheduleSeat(Schedule schedule, Seat seat) {

		ScheduleSeat scheduleSeat = new ScheduleSeat();
		ReflectionTestUtils.setField(scheduleSeat, "schedule", schedule);
		ReflectionTestUtils.setField(scheduleSeat, "seat", seat);

		return scheduleSeat;
	}

	@BeforeEach
	void setUp() {
		// EntityManager 에 있는 영속성 컨텍스트에 객체를 넣음 => ID도 자동 할당됨
		schedule = entityManager.persist(new Schedule());
		seat = entityManager.persist(createSeat(1, "A석", 10000, "A구역"));

		scheduleSeat = entityManager.persist(createScheduleSeat(schedule, seat));

		// 티켓 생성
		ticket = entityManager.persist(
			Ticket.builder()
				.scheduleSeat(scheduleSeat)
				.status(TicketStatus.RESERVED)
				.build());

		entityManager.flush(); // DB로 넘김
		entityManager.clear(); // 영속성 컨텍스트 초기화
	}

	@Test
	@DisplayName("Ticket 저장 및 ID로 조회가 가능하다")
	void saveTicketAndFindById() {
		// given

		// when
		Ticket ticket = ticketRepository.findById(this.ticket.getId()).orElse(null);

		logger.info("로그1 = {}", ticket.getId());
		logger.info("로그2 = {}", scheduleSeat.getId());
		logger.info("로그3 = {}", ticket.getScheduleSeat().getSeat().getPrice());

		// then
		assertThat(ticket).isNotNull();
		assertThat(Objects.requireNonNull(ticket).getId()).isEqualTo(this.ticket.getId());
		assertThat(ticket.getStatus()).isEqualTo(TicketStatus.RESERVED);
		assertThat(ticket.getScheduleSeat().getId()).isEqualTo(scheduleSeat.getId());
	}

}
