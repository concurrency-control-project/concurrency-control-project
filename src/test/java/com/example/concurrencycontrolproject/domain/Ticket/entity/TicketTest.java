package com.example.concurrencycontrolproject.domain.Ticket.entity;

import static com.example.concurrencycontrolproject.domain.seat.entity.seat.Seat.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.concurrencycontrolproject.domain.schedule.entity.Schedule;
import com.example.concurrencycontrolproject.domain.scheduleSeat.entity.ScheduleSeat;
import com.example.concurrencycontrolproject.domain.seat.entity.seat.Seat;
import com.example.concurrencycontrolproject.domain.ticket.entity.Ticket;
import com.example.concurrencycontrolproject.domain.ticket.entity.TicketStatus;
import com.example.concurrencycontrolproject.domain.ticket.exception.TicketException;

// 유닛 테스트
public class TicketTest {

	// 테스트용 ScheduleSeat 생성 메서드
	private ScheduleSeat saveMockScheduleSeat(Long scheduleSeatId) {

		// 테스트용 Schedule 객체
		Schedule mockSchedule = new Schedule();

		// 테스트용 Seat 객체
		Seat mockSeat = of(1, "A석", 10000, "A열");

		ScheduleSeat mockScheduleSeat = new ScheduleSeat();
		ReflectionTestUtils.setField(mockScheduleSeat, "id", scheduleSeatId);
		ReflectionTestUtils.setField(mockScheduleSeat, "schedule", mockSchedule);
		ReflectionTestUtils.setField(mockScheduleSeat, "seat", mockSeat);

		return mockScheduleSeat;
	}

	@Test
	@DisplayName("티켓 생성 시, 상태는 RESERVED 이고 scheduleSeat 값이 일치한다")
	void createTicket_success() {
		// given
		ScheduleSeat mockScheduleSeat = saveMockScheduleSeat(1L); // 실제 엔티티 또는 테스트용 객체

		// when
		Ticket ticket = Ticket.saveTicket(mockScheduleSeat);

		// then
		assertThat(ticket.getStatus()).isEqualTo(TicketStatus.RESERVED);
		assertThat(ticket.getScheduleSeat()).isEqualTo(mockScheduleSeat);
	}

	@Test
	@DisplayName("RESERVED 상태의 티켓을 취소할 수 있다")
	void cancelTicket_whenStatusIsReserve() {
		// given
		ScheduleSeat mockScheduleSeat = saveMockScheduleSeat(1L);
		Ticket ticket = Ticket.builder()
			.scheduleSeat(mockScheduleSeat)
			.status(TicketStatus.RESERVED)
			.build();

		// when
		ticket.cancel();

		// then
		assertThat(ticket.getStatus()).isEqualTo(TicketStatus.CANCELED);
	}

	@Test
	@DisplayName("RESERVED 상태가 아닌 티켓은 취소할 수 없다")
	void cancelTicket_whenStatusIsNotReserved() {
		// given
		ScheduleSeat mockScheduleSeat = saveMockScheduleSeat(1L);

		Ticket ticket = Ticket.builder()
			.scheduleSeat(mockScheduleSeat)
			.status(TicketStatus.CANCELED)
			.build();

		// when, then
		TicketException exception = assertThrows(TicketException.class, ticket::cancel);
		assertThat(exception.getMessage()).isEqualTo("예약된 상태의 티켓만 좌석을 변경할 수 있습니다.");
	}

	@Test
	@DisplayName("RESERVED 상태의 티켓을 만료시킬 수 있다")
	void expireTicket_whenStatusIsReserved() {
		// given
		ScheduleSeat mockScheduleSeat = saveMockScheduleSeat(1L);

		Ticket ticket = Ticket.builder()
			.scheduleSeat(mockScheduleSeat)
			.status(TicketStatus.RESERVED)
			.build();

		// when
		ticket.expire();

		// then
		assertThat(ticket.getStatus()).isEqualTo(TicketStatus.EXPIRED);
	}

	@Test
	@DisplayName("RESERVED 상태가 아닌 티켓은 만료시켜도 상태가 변하지 않는다")
	void expireTicket_whenStatusIsNotReserved() {
		// given
		ScheduleSeat mockScheduleSeat = saveMockScheduleSeat(1L);

		Ticket CanceledTicket = Ticket.builder()
			.scheduleSeat(mockScheduleSeat)
			.status(TicketStatus.CANCELED)
			.build();

		Ticket expiredTicket = Ticket.builder()
			.scheduleSeat(mockScheduleSeat)
			.status(TicketStatus.EXPIRED)
			.build();

		// when
		CanceledTicket.expire();
		expiredTicket.expire();

		// then
		assertThat(CanceledTicket.getStatus()).isEqualTo(TicketStatus.CANCELED);
		assertThat(expiredTicket.getStatus()).isEqualTo(TicketStatus.EXPIRED);
	}

	@Test
	@DisplayName("티켓의 ScheduleSeat 를 변경할 수 있다")
	void changeScheduleSeat_success() {
		// given
		ScheduleSeat mockScheduleSeat1 = saveMockScheduleSeat(1L);
		ScheduleSeat mockScheduleSeat2 = saveMockScheduleSeat(2L);

		Ticket ticket = Ticket.builder()
			.scheduleSeat(mockScheduleSeat1)
			.status(TicketStatus.RESERVED)
			.build();

		// when
		ticket.changeScheduleSeat(mockScheduleSeat2);

		// then
		assertThat(ticket.getScheduleSeat()).isEqualTo(mockScheduleSeat2);
	}

	@Test
	@DisplayName("새로운 ScheduleSeat 가 null 이면 ScheduleSeat 변경 시 예외가 발생한다")
	void changeScheduleSeat_whenNewScheduleSeatIsNull() {
		// given
		ScheduleSeat mockScheduleSeat1 = saveMockScheduleSeat(1L);

		Ticket ticket = Ticket.builder()
			.scheduleSeat(mockScheduleSeat1)
			.status(TicketStatus.RESERVED)
			.build();

		// when, then
		TicketException exception = assertThrows(TicketException.class,
			() -> ticket.changeScheduleSeat(null));

		assertThat(exception.getMessage()).isEqualTo("올바르지 않거나 선택할 수 없는 좌석입니다.");
	}

}
