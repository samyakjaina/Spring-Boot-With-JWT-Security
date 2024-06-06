package com.example.excel.service;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.excel.auth.SignInRequest;

/**
 * Service Interface for Login
 * 
 * @author MEHUL TRIVEDI
 *
 */
@Service
public interface LoginService {

	/**
	 * @param signInRequest
	 * @return
	 */
	ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest signInRequest);
}