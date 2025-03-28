package com.example.concurrencycontrolproject.domain.user.entity;

import java.time.LocalDateTime;

import com.example.concurrencycontrolproject.domain.common.entity.TimeStamped;
import com.example.concurrencycontrolproject.domain.user.enums.UserRole;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "users")
public class User extends TimeStamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String email;

	private String password;

	private String nickname;

	@Enumerated(EnumType.STRING)
	private UserRole role;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Nullable
	private LocalDateTime deletedAt;

	public User() {
	}

	public User(String email, String password, String nickname, String phoneNumber) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.phoneNumber = phoneNumber;
		this.role = UserRole.ROLE_USER;
	}

	public void updateNickname(String nickname) {
		this.nickname = nickname;
	}

	public void updatePhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void updatePassword(String password) {
		this.password = password;
	}

	public void cancelUser() {
		this.deletedAt = LocalDateTime.now();
	}

}
