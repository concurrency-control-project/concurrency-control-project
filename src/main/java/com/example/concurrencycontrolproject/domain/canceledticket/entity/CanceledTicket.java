package com.example.concurrencycontrolproject.domain.canceledticket.entity;

import java.time.LocalDateTime;

import com.example.concurrencycontrolproject.domain.ticket.entity.Ticket;
import com.example.concurrencycontrolproject.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "canceled_tickets")
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CanceledTicket {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long scheduleId;

	@Column(nullable = false)
	private Long seatId;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private LocalDateTime originalCreatedAt;

	@Column(nullable = false)
	private LocalDateTime canceledAt;

	// 정적 팩토리 메서드
	public static CanceledTicket canceledTicket(Ticket ticket, User user) {

		return CanceledTicket.builder()
			.scheduleId(ticket.getScheduleId())
			.seatId(ticket.getSeatId())
			.userId(user.getId())
			.originalCreatedAt(ticket.getCreatedAt())
			.canceledAt(LocalDateTime.now()) // 취소 시간 기록
			.build();
	}

}
