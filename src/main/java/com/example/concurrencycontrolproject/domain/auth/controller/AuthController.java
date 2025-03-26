package com.example.concurrencycontrolproject.domain.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.concurrencycontrolproject.domain.auth.dto.SigninRequest;
import com.example.concurrencycontrolproject.domain.auth.dto.SignupRequest;
import com.example.concurrencycontrolproject.domain.auth.dto.SignupResponse;
import com.example.concurrencycontrolproject.domain.auth.service.AuthService;
import com.example.concurrencycontrolproject.domain.common.response.Response;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/v1/auth/signup")
	public Response<SignupResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
		SignupResponse signup = authService.signup(signupRequest.getEmail(), signupRequest.getPassword(),
			signupRequest.getNickname(), signupRequest.getPhoneNumber());
		return Response.of(signup);
	}

	@PostMapping("/v1/auth/signin")
	public Response<Void> signin(@Valid @RequestBody SigninRequest signinRequest,
		HttpServletResponse servletResponse) {
		authService.signin(signinRequest.getEmail(), signinRequest.getPassword(), servletResponse);
		return Response.empty();
	}
}
