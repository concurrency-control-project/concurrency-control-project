package com.example.concurrencycontrolproject.domain.Ticket.entity;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.concurrencycontrolproject.domain.ticket.entity.Ticket;
import com.example.concurrencycontrolproject.domain.ticket.entity.TicketStatus;
import com.example.concurrencycontrolproject.domain.ticket.exception.TicketErrorCode;
import com.example.concurrencycontrolproject.domain.ticket.exception.TicketException;

// 유닛 테스트
public class TicketTest {

	@Test
	@DisplayName("티켓 생성 시, 상태는 RESERVED 이고 scheduleId, seatId 값 일치한다")
	void createTicket_success() {
		// given
		Long scheduleId = 1L;
		Long seatId = 10L;

		// when
		Ticket ticket = Ticket.saveTicket(scheduleId, seatId);

		// then
		assertThat(ticket.getStatus()).isEqualTo(TicketStatus.RESERVED);
		assertThat(ticket.getScheduleId()).isEqualTo(scheduleId);
		assertThat(ticket.getSeatId()).isEqualTo(seatId);
	}

	@Test
	@DisplayName("RESERVED 상태의 티켓을 만료시킬 수 있다")
	void expireTicket_whenStatusIsReserved() {
		// given
		Ticket ticket = Ticket.builder()
			.scheduleId(1L)
			.seatId(10L)
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
		Ticket expiredTicket = Ticket.builder()
			.scheduleId(1L)
			.seatId(11L)
			.status(TicketStatus.EXPIRED)
			.build();

		// when
		expiredTicket.expire();

		// then
		assertThat(expiredTicket.getStatus()).isEqualTo(TicketStatus.EXPIRED);
	}

	@Test
	@DisplayName("RESERVED 상태 티켓의 SeatId 를 변경할 수 있다")
	void changeSeat_success() {
		// given
		Ticket ticket = Ticket.builder()
			.scheduleId(1L)
			.seatId(10L)
			.status(TicketStatus.RESERVED)
			.build();

		// when
		ticket.changeScheduledSeat(11L);

		// then
		assertThat(ticket.getSeatId()).isEqualTo(11L);
	}

	@Test
	@DisplayName("새로운 ScheduleId 또는 SeatId 가 null 이면 변경 시 예외가 발생한다")
	void changeSeat_whenNewIdIsNull() {
		// given
		Ticket ticket = Ticket.builder()
			.scheduleId(1L)
			.seatId(10L)
			.status(TicketStatus.RESERVED)
			.build();

		// when, then
		TicketException e = assertThrows(TicketException.class,
			() -> ticket.changeScheduledSeat(null));

		// Check against the actual error code/message if possible
		assertThat(e.getErrorCode()).isEqualTo(TicketErrorCode.SCHEDULE_SEAT_BAD_REQUEST);
	}

}
