package com.example.concurrencycontrolproject.domain.ticket.entity;

public enum TicketStatus {
	RESERVED, // 티켓 예매
	CANCELED, // 티켓 취소(환불)
	EXPIRED // 공연이 끝나서 만료됨
}
