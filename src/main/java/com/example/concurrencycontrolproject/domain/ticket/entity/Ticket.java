package com.example.concurrencycontrolproject.domain.ticket.entity;

import com.example.concurrencycontrolproject.domain.common.entity.TimeStamped;
import com.example.concurrencycontrolproject.domain.ticket.exception.TicketErrorCode;
import com.example.concurrencycontrolproject.domain.ticket.exception.TicketException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "tickets",
	uniqueConstraints = {
		@UniqueConstraint(name = "UK_ticket_schedule_seat", columnNames = {"schedule_id", "seat_id"})
	}) // 순차적 락 획득 + 검증/커밋 타이밍 문제 때문에 유니크 설정 해줘야됨
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket extends TimeStamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "schedule_id", nullable = false)
	private Long scheduleId;

	@Column(name = "seat_id", nullable = false)
	private Long seatId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TicketStatus status;

	// 정적 팩토리 메서드
	public static Ticket saveTicket(Long scheduleId, Long seatId) {

		if (scheduleId == null || seatId == null) {
			throw new TicketException(TicketErrorCode.SCHEDULE_SEAT_BAD_REQUEST);
		}

		return Ticket.builder()
			.scheduleId(scheduleId)
			.seatId(seatId)
			.status(TicketStatus.RESERVED) // 기본값
			.build();
	}

	// 티켓 만료 인스턴스 메서드
	public void expire() {
		if (this.status == TicketStatus.RESERVED) {
			this.status = TicketStatus.EXPIRED;
		}
	}

	// 좌석 변경
	public void changeScheduledSeat(Long newSeatId) {
		if (newSeatId == null) {
			throw new TicketException(TicketErrorCode.SCHEDULE_SEAT_BAD_REQUEST);
		}
		this.seatId = newSeatId;

	}

}
