package com.example.excel.auth;

import java.io.Serializable;

/**
 * @author SAMYAK JAIN
 *
 */
public class SignInRequest implements Serializable {
	private static final long serialVersionUID = 2636936156391265891L;
	private String username;
	private String password;

	public SignInRequest() {
	}

	public SignInRequest(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
