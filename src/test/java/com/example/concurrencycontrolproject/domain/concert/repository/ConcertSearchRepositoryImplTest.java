package com.example.concurrencycontrolproject.domain.concert.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.example.concurrencycontrolproject.config.QueryDslConfig;
import com.example.concurrencycontrolproject.domain.concert.entity.Concert;
import com.example.concurrencycontrolproject.domain.concert.entity.ConcertStatus;

@DataJpaTest
@Import(QueryDslConfig.class)
@ActiveProfiles("test")
class ConcertSearchRepositoryImplTest {

	@Autowired
	private ConcertSearchRepositoryImpl concertSearchRepository;

	@Autowired
	private ConcertRepository concertRepository;

	@BeforeEach
	void setUp() {
		Concert concert1 = Concert.builder()
			.title("수정 테스트1")
			.performer("아이유1")
			.location("단독 콘서트1")
			.build();

		Concert concert2 = Concert.builder()
			.title("수정 테스트2")
			.performer("아이유2")
			.location("단독 콘서트2")
			.build();

		concertRepository.saveAll(List.of(concert1, concert2));
	}

	@Test
	public void 콘서트_조회_삭제된_콘서트는_조회될_수_없다() {

		//given
		String keyword = "삭제 테스트3";
		Pageable pageable = PageRequest.of(0, 10);
		//when

		Concert concert = Concert.builder()
			.title("삭제 테스트3")
			.performer("아이유2")
			.location("단독 콘서트2")
			.build();

		concert.softDelete();
		concertRepository.save(concert);
		Page<Concert> concerts = concertSearchRepository.search(keyword, pageable);
		//then
		assertThat(concerts.getTotalElements()).isEqualTo(0);
	}

	@Test
	public void 콘서트_조회_키워드로_조회된다() {

		//given

		String keyword = "아이유1";
		Pageable pageable = PageRequest.of(0, 10);
		//when

		Page<Concert> concerts = concertSearchRepository.search(keyword, pageable);
		//then
		assertThat(concerts.getTotalElements()).isEqualTo(1);
		assertThat(concerts.getContent().get(0)).extracting(
			"title",
			"performer",
			"location",
			"status"
		).containsExactly(
			"수정 테스트1",
			"아이유1",
			"단독 콘서트1",
			ConcertStatus.PLANNED
		);
	}

	@Test
	public void 콘서트_조회_검색_조건_없으면_전체_조회된다() {

		//given

		String keyword = "";
		Pageable pageable = PageRequest.of(0, 10);
		//when

		Page<Concert> concerts = concertSearchRepository.search(keyword, pageable);
		//then
		assertThat(concerts.getTotalElements()).isEqualTo(2);
		assertThat(concerts.getContent().get(0)).extracting(
			"title",
			"performer",
			"location"
		).containsExactly(
			"수정 테스트1",
			"아이유1",
			"단독 콘서트1"
		);

	}

}