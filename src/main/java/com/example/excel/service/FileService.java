package com.example.excel.service;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.excel.domain.ExcelFileEntity;
import com.example.excel.exceptions.CustomException;

/**
 * @author BT
 *
 */
@Service
public interface ExcelFileService {
	/**
	 * It will delete the Excel File by Id
	 * 
	 * @param id
	 * @return
	 * @throws CustomException
	 */
	String deleteById(Long id) throws CustomException;

	/**
	 * It will fetch all the Excel File Record
	 * 
	 * @return
	 */
	List<ExcelFileEntity> getAllRecords();

	/**
	 * It will find the Excel File by Id
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws CustomException
	 */
	ExcelFileEntity findById(Long id, HttpServletRequest request) throws CustomException;

	/**
	 * It will upload the Excel File
	 * 
	 * @param file
	 * @param request
	 * @return
	 * @throws IOException
	 */
	String uploadFile(MultipartFile file, HttpServletRequest request) throws IOException;

	/**
	 * It will fetch the Current status of Excel File by id
	 * 
	 * @param id
	 * @return
	 * @throws CustomException
	 */
	String findByIdStatus(Long id) throws CustomException;

}
