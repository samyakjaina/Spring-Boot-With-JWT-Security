package com.example.excel.constant;

/**
 * @author MEHUL TRIVEDI
 *
 */
public enum ResponseMessage {

	Null("No Data Found");
	private String message;
	private ResponseMessage( String message) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
}
