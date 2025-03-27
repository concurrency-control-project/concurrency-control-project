package com.example.concurrencycontrolproject.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SigninRequest {

	@NotBlank
	@Email
	private String email;

	@NotBlank
	private String password;

}
