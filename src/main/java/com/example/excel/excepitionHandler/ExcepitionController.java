package com.example.excel.excepitionHandler;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.excel.exceptions.CustomException;

/**
 * @author SAMYAK JAIN
 *
 */
@RestControllerAdvice
public class ExcepitionController {
	
	@ExceptionHandler(value = CustomException.class)
	public ResponseEntity<Object> exception(CustomException exception) {
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<List<Object>> processUnmergeException(MethodArgumentNotValidException ex) {
		return new ResponseEntity<>(ex.getBindingResult().getAllErrors().stream()
				.map(fieldError -> fieldError.getDefaultMessage()).collect(Collectors.toList()),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = AccessDeniedException.class)
	public ResponseEntity<Object> exception(AccessDeniedException exception) {
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(value = IOException.class)
	public ResponseEntity<Object> exception(IOException exception) {
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_IMPLEMENTED);
	}
}
