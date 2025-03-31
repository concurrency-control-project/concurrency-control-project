package com.example.concurrencycontrolproject.domain.user.entity;

import java.time.LocalDateTime;

import com.example.concurrencycontrolproject.domain.user.enums.SocialType;
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
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Table(name = "user")
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
	@Enumerated(EnumType.STRING)
	private SocialType social;
	@Nullable
	private LocalDateTime deletedAt;

	public User() {
	}

	@Builder
	public User(String email, String nickname, UserRole role, SocialType social) {
		this.email = email;
		this.nickname = nickname;
		this.role = role;
		this.social = social;
	}

	public User(String email, String password, String nickname, String phoneNumber) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.phoneNumber = phoneNumber;
		this.role = UserRole.ROLE_USER;
		this.social = SocialType.NONE;
	}

	public User(String email, String password, String nickname, String phoneNumber, SocialType social) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.phoneNumber = phoneNumber;
		this.role = UserRole.ROLE_USER;
		this.social = social;
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

	public void updateGuestUser() {
		this.role = UserRole.ROLE_USER;
	}

	public void cancelUser() {
		this.deletedAt = LocalDateTime.now();
	}

}
