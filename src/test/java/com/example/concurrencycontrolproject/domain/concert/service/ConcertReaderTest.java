package com.example.concurrencycontrolproject.domain.concert.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.concurrencycontrolproject.domain.concert.entity.Concert;
import com.example.concurrencycontrolproject.domain.concert.entity.ConcertStatus;
import com.example.concurrencycontrolproject.domain.concert.exception.ConcertNotFoundException;
import com.example.concurrencycontrolproject.domain.concert.repository.ConcertRepository;
import com.example.concurrencycontrolproject.domain.concert.support.ConcertTestBuilder;

@ExtendWith(MockitoExtension.class)
class ConcertReaderTest {

	@Mock
	private ConcertRepository concertRepository;

	@InjectMocks
	private ConcertReader concertReader;

	@Test
	public void 콘서트_조회_존재하지_않는_콘서트_조회시_ConcertNotFoundException_예외_발생한다() {
		//given
		Long concertId = 1L;
		given(concertRepository.findById(any(Long.class))).willReturn(Optional.empty());
		//when & then
		assertThatThrownBy(() -> concertReader.read(concertId)).isInstanceOf(
				ConcertNotFoundException.class)
			.hasMessage("콘서트를 찾을 수 없습니다");

	}

	@Test
	public void 콘서트_조회_정상적으로_조회된다() {
		//given
		Long concertId = 1L;
		Concert concert = defaultTestBuilder().buildConcert();
		given(concertRepository.findById(any(Long.class))).willReturn(Optional.of(concert));
		//when
		Concert result = concertReader.read(concertId);
		//then
		assertThat(result).extracting(
			"id", "title", "location", "description",
			"concertStartDateTime",
			"concertEndDateTime"
		).containsExactly(
			1L, "아이유콘서트", "올림픽 공원", "단독 콘서트",
			LocalDateTime.of(2025, 3, 20, 0, 0),
			LocalDateTime.of(2025, 3, 24, 0, 0)
		);

	}

	private ConcertTestBuilder defaultTestBuilder() {
		return new ConcertTestBuilder()
			.withTitle("아이유콘서트")
			.withPerformer("아이유")
			.withDescription("단독 콘서트")
			.withConcertStartDateTime(LocalDateTime.of(2025, 3, 20, 0, 0))
			.withConcertEndDateTime(LocalDateTime.of(2025, 3, 24, 0, 0))
			.withBookingStartDateTime(LocalDateTime.of(2025, 3, 10, 0, 0))
			.withBookingEndDateTime(LocalDateTime.of(2025, 3, 20, 0, 0))
			.withLocation("올림픽 공원")
			.withStatus(ConcertStatus.PLANNED)
			.withId(1L);
	}
}