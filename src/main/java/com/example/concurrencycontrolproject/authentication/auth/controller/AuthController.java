package com.example.concurrencycontrolproject.authentication.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.concurrencycontrolproject.authentication.auth.dto.AdditionalInfoRequest;
import com.example.concurrencycontrolproject.authentication.auth.dto.SigninRequest;
import com.example.concurrencycontrolproject.authentication.auth.dto.SignupRequest;
import com.example.concurrencycontrolproject.authentication.auth.dto.SignupResponse;
import com.example.concurrencycontrolproject.authentication.auth.dto.UserProfileResponse;
import com.example.concurrencycontrolproject.authentication.auth.service.AuthService;
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

	// 첫 소셜 로그인 유저 -> 추가 정보 입력
	@PostMapping("/v1/auth/privacy-consent")
	public Response<UserProfileResponse> agreeToPrivacyPolicy(@Valid @RequestBody AdditionalInfoRequest addInfo) {
		UserProfileResponse userProfile = authService.addUserInfo(addInfo);
		return Response.of(userProfile);
	}
}
