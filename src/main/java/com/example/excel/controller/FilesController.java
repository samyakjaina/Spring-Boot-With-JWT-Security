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
import com.example.excel.service.impl.ExcelFileServiceImpl;

/**
 * @author BT
 *
 */
@RestController
@RequestMapping("/file")
public class FilesController {

	@Autowired
	ExcelFileServiceImpl excelFileServiceImpl;

	
	/**
	 * It will fetch all the Excel File Record
	 * @return
	 */
	@GetMapping("/getAllRecords")
	@PreAuthorize("hasRole('ROLE_USER')  or hasRole('ROLE_ADMIN') ")
	public List<ExcelFileEntity> getAllRecords() {
		return excelFileServiceImpl.getAllRecords();
	}

	/**
	 * It will delete the Excel File by Id
	 * @param id
	 * @return
	 * @throws CustomException
	 */
	@DeleteMapping("/deleteById/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String deleteById(@PathVariable Long id) throws CustomException {
		return excelFileServiceImpl.deleteById(id);
	}

	/**
	 * It will find the Excel File by Id
	 * @param id
	 * @param request
	 * @return
	 * @throws CustomException
	 */
	@GetMapping("/findById/{id}")
	@PreAuthorize("hasRole('ROLE_USER')  or hasRole('ROLE_ADMIN') ")
	public ExcelFileEntity findById(@PathVariable Long id, HttpServletRequest request) throws CustomException {
		return excelFileServiceImpl.findById(id, request);
	}

	/**
	 * It will upload the Excel File
	 * @param file
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/upload")
	@PreAuthorize("hasRole('ROLE_ADMIN') ")
	public String upload(@RequestParam MultipartFile file, HttpServletRequest request) throws Exception {
		return excelFileServiceImpl.uploadFile(file, request);
	}

	/**
	 * It will fetch the Current status of Excel File by id
	 * @param id
	 * @return
	 * @throws CustomException
	 */
	@GetMapping("/findByIdStatus/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN') ")
	public String findByIdStatus(@PathVariable Long id) throws CustomException {
		return excelFileServiceImpl.findByIdStatus(id);
	}
}
