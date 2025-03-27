package com.example.concurrencycontrolproject.domain.seat.entity.seat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "seat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}

