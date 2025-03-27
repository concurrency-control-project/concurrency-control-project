package com.example.concurrencycontrolproject.domain.concert.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.concurrencycontrolproject.domain.concert.entity.Concert;
import com.example.concurrencycontrolproject.domain.concert.entity.ConcertStatus;
import com.example.concurrencycontrolproject.domain.concert.repository.ConcertRepository;

@ExtendWith(MockitoExtension.class)
class ConcertStatusSchedulerServiceTest {

	@Mock
	private ConcertRepository concertRepository;

	@InjectMocks
	private ConcertStatusSchedulerService concertStatusSchedulerService;

	@Nested
	class UpdateConcertStatus {

		@Test
		public void 콘서트_상태_수정_요청시간이_콘서트_예약시작시간_이전이면_상태는_PLANNED로_변경된다() {
			//given
			LocalDateTime bookingStartDateTime = LocalDateTime.of(2025, 3, 2, 0, 0, 0);
			Concert concert = Concert.builder().bookingStartDateTime(bookingStartDateTime).build();
			ReflectionTestUtils.setField(concert, "status", ConcertStatus.BOOKING_OPEN);
			given(concertRepository.findByStatusNotIn(anyList())).willReturn(List.of(concert));

			LocalDateTime requestDateTime = LocalDateTime.of(2025, 3, 1, 0, 0, 0);
			//when
			concertStatusSchedulerService.updateConcertsStatus(requestDateTime);
			//then
			assertThat(concert.getStatus()).isEqualTo(ConcertStatus.PLANNED);
		}

		@Test
		public void 콘서트_상태_수정_요청시간이_콘서트_예약시작시간이후_예약종료시간_이전이면_상태는_BOOKING_OPEN로_변경된다() {
			//given
			LocalDateTime bookingStartDateTime = LocalDateTime.of(2025, 3, 2, 0, 0, 0);
			LocalDateTime bookingEndDateTime = LocalDateTime.of(2025, 3, 4, 0, 0, 0);
			Concert concert = Concert.builder()
				.bookingStartDateTime(bookingStartDateTime)
				.bookingEndDateTime(bookingEndDateTime)
				.build();
			ReflectionTestUtils.setField(concert, "status", ConcertStatus.PLANNED);
			given(concertRepository.findByStatusNotIn(anyList())).willReturn(List.of(concert));

			LocalDateTime requestDateTime = LocalDateTime.of(2025, 3, 3, 0, 0, 0);
			//when
			concertStatusSchedulerService.updateConcertsStatus(requestDateTime);
			//then
			assertThat(concert.getStatus()).isEqualTo(ConcertStatus.BOOKING_OPEN);
		}

		@Test
		public void 콘서트_상태_수정_요청시간이_콘서트_시작시간_이후_종료시간_이전이면_상태는_PERFORMING로_변경된다() {
			//given
			LocalDateTime bookingStartDateTime = LocalDateTime.of(2025, 3, 2, 0, 0, 0);
			LocalDateTime bookingEndDateTime = LocalDateTime.of(2025, 3, 4, 0, 0, 0);
			LocalDateTime concertStartDateTime = LocalDateTime.of(2025, 3, 5, 0, 0, 0);
			LocalDateTime concertEndDateTime = LocalDateTime.of(2025, 3, 7, 0, 0, 0);
			Concert concert = Concert.builder()
				.bookingStartDateTime(bookingStartDateTime)
				.bookingEndDateTime(bookingEndDateTime)
				.concertStartDateTime(concertStartDateTime)
				.concertEndDateTime(concertEndDateTime)
				.build();
			ReflectionTestUtils.setField(concert, "status", ConcertStatus.PLANNED);
			given(concertRepository.findByStatusNotIn(anyList())).willReturn(List.of(concert));

			LocalDateTime requestDateTime = LocalDateTime.of(2025, 3, 6, 0, 0, 0);
			//when
			concertStatusSchedulerService.updateConcertsStatus(requestDateTime);
			//then
			assertThat(concert.getStatus()).isEqualTo(ConcertStatus.PERFORMING);
		}

		@Test
		public void 콘서트_상태_수정_요청시간이_콘서트_종료시간_이후이면_상태는_FINISHED_로_변경된다() {
			//given
			LocalDateTime bookingStartDateTime = LocalDateTime.of(2025, 3, 2, 0, 0, 0);
			LocalDateTime bookingEndDateTime = LocalDateTime.of(2025, 3, 4, 0, 0, 0);
			LocalDateTime concertStartDateTime = LocalDateTime.of(2025, 3, 5, 0, 0, 0);
			LocalDateTime concertEndDateTime = LocalDateTime.of(2025, 3, 7, 0, 0, 0);
			Concert concert = Concert.builder()
				.bookingStartDateTime(bookingStartDateTime)
				.bookingEndDateTime(bookingEndDateTime)
				.concertStartDateTime(concertStartDateTime)
				.concertEndDateTime(concertEndDateTime)
				.build();
			ReflectionTestUtils.setField(concert, "status", ConcertStatus.PLANNED);
			given(concertRepository.findByStatusNotIn(anyList())).willReturn(List.of(concert));

			LocalDateTime requestDateTime = LocalDateTime.of(2025, 3, 8, 0, 0, 0);
			//when
			concertStatusSchedulerService.updateConcertsStatus(requestDateTime);
			//then
			assertThat(concert.getStatus()).isEqualTo(ConcertStatus.FINISHED);
		}
	}

}