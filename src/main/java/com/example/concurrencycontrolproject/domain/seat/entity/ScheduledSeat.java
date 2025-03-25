package com.example.concurrencycontrolproject.domain.seat.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "schedule_seat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledSeat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long scheduleId;
	private Long seatId;
	private Boolean isAssigned;
}

