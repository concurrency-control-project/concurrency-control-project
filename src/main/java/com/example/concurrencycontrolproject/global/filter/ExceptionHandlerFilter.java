package com.example.concurrencycontrolproject.global.filter;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import com.example.concurrencycontrolproject.domain.auth.exception.AuthException;
import com.example.concurrencycontrolproject.domain.common.exception.ErrorCode;
import com.example.concurrencycontrolproject.domain.common.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ExceptionHandlerFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (AuthException e) {
			setErrorResponse(response, e.getErrorCode());
		}
	}

	private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode) {
		ObjectMapper objectMapper = new ObjectMapper();

		response.setStatus(errorCode.getHttpStatus().value());
		response.setContentType("application/json; charset=UTF-8");
		ErrorResponse errorResponse = ErrorResponse.of(errorCode.getCode(), errorCode.getDefaultMessage());

		try {
			response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
