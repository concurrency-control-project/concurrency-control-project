package com.example.concurrencycontrolproject.domain.schedule.entity;

import static lombok.AccessLevel.*;

import java.time.LocalDateTime;

import com.example.concurrencycontrolproject.domain.concert.entity.Concert;
import com.example.concurrencycontrolproject.domain.schedule.enums.ScheduleStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "schedules")
public class Schedule extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManytoOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "concert_id", nullable = false)
	private Concert concert;

	private LocalDateTime datetime;

	@Enumerated(EnumType.STRING)
	private ScheduleStatus status;

	private LocalDateTime deletedAt;

	public Schedule(Concert concert, LocalDateTime datetime, ScheduleStatus status) {
		this.concert = concert;
		this.datetime = datetime;
		this.status = status;
	}

	public void updateDateTime(LocalDateTime datetime) {
		this.datetime = datetime;
	}

	public void updateStatus(ScheduleStatus status) {
		this.status = status;
	}
}
