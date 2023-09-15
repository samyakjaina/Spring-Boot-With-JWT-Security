package com.example.excel.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.excel.auth.TokenManager;
import com.example.excel.constant.ResponseMessage;
import com.example.excel.domain.ExcelFileEntity;
import com.example.excel.domain.FileDataEntity;
import com.example.excel.domain.User;
import com.example.excel.exceptions.CustomException;
import com.example.excel.repository.ExcelFileRepository;
import com.example.excel.repository.UserRepository;
import com.example.excel.security.AuthTokenFilter;
import com.example.excel.service.ExcelFileService;

/**
 * @author BT
 *
 */
@Component
public class ExcelFileServiceImpl implements ExcelFileService {

	@Autowired
	private ExcelFileRepository excelFileRepository;

	@Autowired
	private AuthTokenFilter authTokenFilter;

	@Autowired
	private TokenManager tokenManager;

	@Autowired
	UserRepository userRepository;

	@Value("${excel.file.path}")
	private String excelFilePath;

	@Override
	public String deleteById(Long id) throws CustomException {
		ExcelFileEntity excelFile = excelFileRepository.findByIdAndDeletedFalse(id)
				.orElseThrow(() -> new CustomException(ResponseMessage.Null));

		// deleting the temproary file
		File tempFile = new File(excelFilePath + excelFile.getFileName());
		if (tempFile.exists()) {
			tempFile.delete();
		}
		excelFile.setDeleted(true);
		excelFileRepository.save(excelFile);
		return "Deleted SuccessFully";
	}

	@Override
	public List<ExcelFileEntity> getAllRecords() {
		return excelFileRepository.findByDeletedFalse();
	}

	@Override
	public ExcelFileEntity findById(Long id, HttpServletRequest request) throws CustomException {
		ExcelFileEntity excelFile = excelFileRepository.findByIdAndDeletedFalse(id)
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
		excelFileRepository.save(temp);
		excelFile.setLastAccess(storedDate);
		excelFile.setUser(storedUser);

		return excelFile;
	}

	@Override
	public String uploadFile(MultipartFile file, HttpServletRequest request)
			throws IOException, UsernameNotFoundException {
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

		// Getting the Sheet
		Sheet sheet = workbook.getSheetAt(0);
		// Create a DataFormatter to format and get each cell's value as String
		DataFormatter dataFormatter = new DataFormatter();
		// 1. You can obtain a rowIterator and columnIterator and iterate over them
		Iterator<Row> rowIterator = sheet.rowIterator();

		ExcelFileEntity excelFile = new ExcelFileEntity();
		excelFile.setUser(user);
		excelFile.setFileName(fileName);
		excelFile.setLastAccess(LocalDateTime.now());
		excelFile.setStatus("upload started");
		excelFile = excelFileRepository.save(excelFile);
		String id = excelFile.getId().toString();

		Future<String> future = asyncMethodWithReturnType(workbook, excelFile, rowIterator, dataFormatter);
		return id;
	}

	private User findUserByrequest(HttpServletRequest request) throws UsernameNotFoundException {

		String jwt = authTokenFilter.parseJwt(request);
		String username = tokenManager.getUsernameFromToken(jwt);
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

	}

	@Override
	public String findByIdStatus(Long id) throws CustomException {

		String status = excelFileRepository.findStausByIdAndDeletedFalse(id);
		return status;
	}

	@Async
	public Future<String> asyncMethodWithReturnType(Workbook workbook, ExcelFileEntity excelFile,
			Iterator<Row> rowIterator, DataFormatter dataFormatter) throws FileNotFoundException, IOException {
		int count = 0;
		List<FileDataEntity> fileData = new ArrayList<>();

		// saving File temproary .
		File tempFile = new File(excelFilePath + excelFile.getFileName());
		try {
			tempFile.createNewFile();
			try (FileOutputStream fos = new FileOutputStream(tempFile)) {
				workbook.write(fos);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		excelFile.setStatus("upload is in progress");
		excelFile = excelFileRepository.save(excelFile);

		// Iterating each row
		while (rowIterator.hasNext()) {

			Row row = rowIterator.next();
			// Now let's iterate over the columns of the current row
			Iterator<Cell> cellIterator = row.cellIterator();
			StringBuilder data = new StringBuilder();

			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				String cellValue = dataFormatter.formatCellValue(cell);
				data.append(cellValue).append(",");
			}

			if (data.length() == 0) {
				continue;
			} else if (count == 0) {
				excelFile.setColumnName(data.toString().substring(0, data.length() - 1));
				count++;
			} else if (data.length() > 0) {
				FileDataEntity row_data = new FileDataEntity();
				row_data.setData(data.toString().substring(0, data.length() - 1));
				fileData.add(row_data);
			}

		}
		excelFile.setFileData(fileData);
		excelFile.setStatus("upload successfully");
		excelFile = excelFileRepository.save(excelFile);

		workbook.close();

		return new AsyncResult<String>("Excel Uplaoded Successfully");
	}

}
