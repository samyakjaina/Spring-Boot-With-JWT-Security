package com.example.demo.exceptions;

import com.example.demo.constant.ResponseMessage;


/**
 * @author BT
 *
 */
public class CustomException extends Exception {
	private static final long serialVersionUID = 1L;

	public CustomException(String message) {
		super(message);
	}

	public CustomException(ResponseMessage message) {
		super(message.getMessage());
	}
}
