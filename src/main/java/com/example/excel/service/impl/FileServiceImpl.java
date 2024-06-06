package com.example.excel.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.excel.auth.TokenManager;
import com.example.excel.constant.ResponseMessage;
import com.example.excel.domain.ExcelFileEntity;
import com.example.excel.domain.User;
import com.example.excel.exceptions.CustomException;
import com.example.excel.repository.FileRepository;
import com.example.excel.repository.UserRepository;
import com.example.excel.security.AuthTokenFilter;
import com.example.excel.service.FileService;

/**
 * Service Implementaion Class for Excel file
 * 
 * @author SAMYAK JAIN
 *
 */
@Component
public class FileServiceImpl implements FileService {

	@Autowired
	private FileRepository fileRepository;

	@Autowired
	private AuthTokenFilter authTokenFilter;

	@Autowired
	private TokenManager tokenManager;

	@Autowired
	UserRepository userRepository;

	@Value("${excel.file.path}")
	private String excelFilePath;

	static final Logger LOGGER = LogManager.getLogger(FileServiceImpl.class);

	@Override
	public String deleteById(Long id) throws CustomException {
		LOGGER.info("Deleting Excel File of id " + id);
		ExcelFileEntity excelFile = fileRepository.findByIdAndDeletedFalse(id)
				.orElseThrow(() -> new CustomException(ResponseMessage.Null));

		// deleting the temproary file
		File tempFile = new File(excelFilePath + excelFile.getFileName());
		if (tempFile.exists()) {
			tempFile.delete();
		}
		excelFile.setDeleted(true);
		fileRepository.save(excelFile);
		LOGGER.info("Excel File deleted successfully " + excelFile.toString());
		return "Deleted SuccessFully";
	}

	@Override
	public List<ExcelFileEntity> getAllRecords() {
		return fileRepository.findByDeletedFalse();
	}

	@Override
	public ExcelFileEntity findById(Long id, HttpServletRequest request) throws CustomException {
		LOGGER.info("Fetching Excel File Details of id " + id);
		ExcelFileEntity excelFile = fileRepository.findByIdAndDeletedFalse(id)
				.orElseThrow(() -> new CustomException(ResponseMessage.Null));
		User storedUser = excelFile.getUser();
		LocalDateTime storedDate = excelFile.getLastAccess();
		ExcelFileEntity temp = null;
		try {
			temp = (ExcelFileEntity) excelFile.clone();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// finding the user based on request
		User user = findUserByrequest(request);
		temp.setUser(user);
		temp.setLastAccess(LocalDateTime.now());
		fileRepository.save(temp);
		excelFile.setLastAccess(storedDate);
		excelFile.setUser(storedUser);

		return excelFile;
	}

	private User findUserByrequest(HttpServletRequest request) throws UsernameNotFoundException {
		LOGGER.info("Fetching the user by Http Request");
		String jwt = authTokenFilter.parseJwt(request);
		String username = tokenManager.getUsernameFromToken(jwt);
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

	}

	@Override
	public String findByIdStatus(Long id) throws CustomException {
		LOGGER.info("Fetching the status of Excel File of id " + id);
		String status = fileRepository.findStausByIdAndDeletedFalse(id);
		return status;
	}

	@Override
	public Long uploadFile(MultipartFile file, HttpServletRequest request)
			throws IOException, UsernameNotFoundException {
		LOGGER.info("Excel File Uploading Method started");
		Workbook workbook = null;
		String fileName = file.getOriginalFilename();
		String extension[] = fileName.split(Pattern.quote("."));
		if (extension[1].equals("xls")) {
			workbook = new HSSFWorkbook(file.getInputStream());
		} else {
			workbook = new XSSFWorkbook(file.getInputStream());
		}
		// finding the user based on request
		User user = findUserByrequest(request);

		// saving File temproary .
		File tempFile = new File(excelFilePath + file.getOriginalFilename());
		try {
			tempFile.createNewFile();
			try (FileOutputStream fos = new FileOutputStream(tempFile)) {
				workbook.write(fos);
			}
		} catch (IOException e) {
			LOGGER.error("Problem with saving the file " + file.getOriginalFilename() + " into the temproary location",
					e);
		}
		ExcelFileEntity excelFile = new ExcelFileEntity();
		excelFile.setUser(user);
		excelFile.setFileName(file.getOriginalFilename());
		excelFile.setLastAccess(LocalDateTime.now());
		excelFile.setStatus("saved");
		excelFile = fileRepository.save(excelFile);
		workbook.close();
		return excelFile.getId();
	}

}
