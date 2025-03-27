package com.example.concurrencycontrolproject.domain.ticket.entity;

import com.example.concurrencycontrolproject.domain.common.entity.TimeStamped;
import com.example.concurrencycontrolproject.domain.scheduleSeat.entity.ScheduleSeat;
import com.example.concurrencycontrolproject.domain.ticket.exception.TicketErrorCode;
import com.example.concurrencycontrolproject.domain.ticket.exception.TicketException;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "tickets")
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket extends TimeStamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schedule_seat_id", nullable = false)
	private ScheduleSeat scheduleSeat;

	// private ScheduledSeat scheduledSeat;

	@Enumerated(EnumType.STRING)
	private TicketStatus status;

	// 정적 팩토리 메서드
	public static Ticket saveTicket(ScheduleSeat scheduleSeat) {
		return Ticket.builder()
			.scheduleSeat(scheduleSeat)
			.status(TicketStatus.RESERVED) // 기본값
			.build();
	}

	// 티켓 취소 인스턴스 메서드
	public void cancel() {
		if (this.status != TicketStatus.RESERVED) {
			throw new TicketException(TicketErrorCode.TICKET_UPDATE_INVALID_STATUS);
		}
		this.status = TicketStatus.CANCELED;
	}

	// 티켓 만료 인스턴스 메서드
	public void expire() {
		if (this.status == TicketStatus.RESERVED) {
			this.status = TicketStatus.EXPIRED;
		}
	}

	// 좌석 변경
	public void changeScheduleSeat(ScheduleSeat newScheduleSeat) {
		if (newScheduleSeat == null) {
			throw new TicketException(TicketErrorCode.SCHEDULE_SEAT_BAD_REQUEST);
		}
		this.scheduleSeat = newScheduleSeat;

	}

}
