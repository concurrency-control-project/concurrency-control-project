package com.example.concurrencycontrolproject.domain.schedule.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.concurrencycontrolproject.domain.schedule.entity.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

	// 중복된 스케줄 확인
	boolean existsByConcertIdAndDateTime(Long concertId, LocalDateTime dateTime);

	// 관리자에게 모든 상태 노출, datetime에서 날짜만 추출하여 조회
	@EntityGraph(attributePaths = "concert")
	@Query("SELECT s FROM Schedule s WHERE s.concert.id = :concertId AND s.dateTime >= :start AND s.dateTime < :end")
	Page<Schedule> findByConcertIdAndDatetime(
		@Param("concertId") Long concertId,
		@Param("start") LocalDateTime start,
		@Param("end") LocalDateTime end,
		Pageable pageable
	);

	// 사용자에게 ACTIVE 상태만 노출, datetime에서 날짜만 추출하여 조회
	@EntityGraph(attributePaths = "concert")
	@Query("SELECT s FROM Schedule s WHERE s.concert.id = :concertId AND s.dateTime >= :start AND s.dateTime < :end AND s.status = 'ACTIVE'")
	Page<Schedule> findActiveByConcertIdAndDatetime(
		@Param("concertId") Long concertId,
		@Param("start") LocalDateTime start,
		@Param("end") LocalDateTime end,
		Pageable pageable
	);

	Optional<Schedule> findByIdAndConcertId(Long scheduleId, Long concertId);
}
