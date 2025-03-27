package com.example.concurrencycontrolproject.domain.seat.entity.seat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Integer number;
	private String grade;
	private Integer price;
	private String section;

	public void update(int number, String grade, int price, String section) {
		this.number = number;
		this.grade = grade;
		this.price = price;
		this.section = section;
	}

	private Seat(int number, String grade, int price, String section) {  // 🔥 private 생성자 유지
		this.number = number;
		this.grade = grade;
		this.price = price;
		this.section = section;
	}

	public static Seat of(int number, String grade, int price, String section) {
		return new Seat(number, grade, price, section);
	}
}

