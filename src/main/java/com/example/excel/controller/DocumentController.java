package com.example.excel.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.excel.domain.ExcelFileEntity;
import com.example.excel.exceptions.CustomException;
import com.example.excel.service.FileService;

/**
 * Controller class for Files
 * 
 * @author SAMYAK JAIN
 *
 */
@RestController
@RequestMapping("/document")
public class DocumentController {

	@Autowired
	FileService fileService;

	/**
	 * Method to fetch all the document
	 * 
	 * @return
	 */
	@GetMapping("/get/all")
	@PreAuthorize("hasAuthority('USER')  or hasAuthority('ADMIN') ")
	public List<ExcelFileEntity> getAllRecords() {
		return fileService.getAllRecords();
	}

	/**
	 * Method to delete the document by Id
	 * 
	 * @param id
	 * @return
	 * @throws CustomException
	 */
	@DeleteMapping("/delete-by-id/{id}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public String deleteById(@PathVariable Long id) throws CustomException {
		return fileService.deleteById(id);
	}

	/**
	 * Method to find the Excel File by Id
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws CustomException
	 */
	@GetMapping("/find-by-id/{id}")
	@PreAuthorize("hasAuthority('USER')  or hasAuthority('ADMIN') ")
	public ExcelFileEntity findById(@PathVariable Long id, HttpServletRequest request) throws CustomException {
		return fileService.findById(id, request);
	}

	/**
	 * Method to upload the Excel File
	 * 
	 * @param file
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/upload")
	@PreAuthorize("hasAuthority('ADMIN') ")
	public Long upload(@RequestParam MultipartFile file, HttpServletRequest request) throws Exception {
		return fileService.uploadFile(file, request);
	}

	/**
	 * Method to fetch the Current status of Excel File by id
	 * 
	 * @param id
	 * @return
	 * @throws CustomException
	 */
	@GetMapping("/find-by-id-status/{id}")
	@PreAuthorize("hasAuthority('ADMIN') ")
	public String findByIdStatus(@PathVariable Long id) throws CustomException {
		return fileService.findByIdStatus(id);
	}
}
