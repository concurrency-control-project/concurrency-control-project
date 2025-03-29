package com.example.concurrencycontrolproject.domain.canceledticket.dto.response;

import java.time.LocalDateTime;

import com.example.concurrencycontrolproject.domain.canceledticket.entity.CanceledTicket;
import com.example.concurrencycontrolproject.domain.seat.dto.response.SeatResponseDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CanceledTicketResponse {

	private Long id;
	private Long scheduleId;
	private SeatResponseDto seat;
	private LocalDateTime originalCreatedAt; // 원래 티켓 예매 시간
	private LocalDateTime canceledAt; // 환불 시간

	public static CanceledTicketResponse from(CanceledTicket canceledTicket, SeatResponseDto seatDto) {
		return CanceledTicketResponse.builder()
			.id(canceledTicket.getId())
			.scheduleId(canceledTicket.getScheduleId())
			.seat(seatDto)
			.originalCreatedAt(canceledTicket.getOriginalCreatedAt())
			.canceledAt(canceledTicket.getCanceledAt())
			.build();
	}

}