package com.example.concurrencycontrolproject.domain.concert.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.concurrencycontrolproject.domain.concert.entity.Concert;

public interface ConcertSearchRepository {

	Page<Concert> search(String keyword, Pageable pageable);
}
