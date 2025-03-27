package com.example.concurrencycontrolproject.domain.scheduleSeat.response;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.concurrencycontrolproject.domain.scheduleSeat.entity.ScheduleSeat;

public interface ScheduleSeatRepository extends JpaRepository<ScheduleSeat, Long> {

	@Query("SELECT s FROM ScheduleSeat s WHERE s.id = :id AND s.isAssigned = false")
	Optional<ScheduleSeat> findByIdAndAssignedIsFalse(Long id);
}
