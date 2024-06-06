package com.example.excel.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.excel.auth.SignInRequest;
import com.example.excel.service.LoginService;

/**
 * Controller class for Login
 * 
 * @author MEHUL TRIVEDI
 *
 */
@RestController
@RequestMapping("/login")
public class LoginController {
	@Autowired
	LoginService loginService;

	/**
	 * This api is used for sign into the system
	 * 
	 * @param loginRequest
	 * @return
	 */
	@PostMapping("/signin")
	public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest signInRequest) {
		return loginService.signIn(signInRequest);
	}

}
