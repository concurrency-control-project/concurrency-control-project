package com.example.concurrencycontrolproject.domain.schedule.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.concurrencycontrolproject.domain.schedule.entity.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

	// 관리자에게 모든 상태 노출, datetime에서 날짜만 추출하여 조회
	@EntityGraph(attributePaths = "concert")
	@Query("SELECT s FROM Schedule s WHERE s.concert.id = :concertId AND s.dateTime >= :date AND s.dateTime < :date + 1")
	Page<Schedule> findByConcertIdAndDatetime(
		@Param("concertId") Long concertId,
		@Param("date") LocalDate date,
		Pageable pageable
	);

	// 사용자에게 ACTIVE 상태만 노출, datetime에서 날짜만 추출하여 조회
	@EntityGraph(attributePaths = "concert")
	@Query("SELECT s FROM Schedule s WHERE s.concert.id = :concertId AND s.dateTime >= :date AND s.dateTime < :date + 1 AND s.status = 'ACTIVE'")
	Page<Schedule> findActiveByConcertIdAndDatetime(
		@Param("concertId") Long concertId,
		@Param("date") LocalDate date,
		Pageable pageable
	);

	Optional<Schedule> findByIdAndConcertId(Long scheduleId, Long concertId);
}
