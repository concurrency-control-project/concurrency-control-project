package com.example.concurrencycontrolproject.domain.scheduleSeat.entity;

import com.example.concurrencycontrolproject.domain.common.entity.Timestamped;
import com.example.concurrencycontrolproject.domain.schedule.entity.Schedule;
import com.example.concurrencycontrolproject.domain.seat.entity.Seat;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Getter
@Entity
public class ScheduleSeat extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schedule_id", nullable = false)
	private Schedule schedule;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seat_id", nullable = false)
	private Seat seat;

	private boolean isAssigned = false;

	public void assign() {
		this.isAssigned = true;
	}

}
