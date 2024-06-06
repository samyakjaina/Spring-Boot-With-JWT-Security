package com.example.excel.scheduler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.example.excel.domain.ExcelFileEntity;
import com.example.excel.domain.FileDataEntity;
import com.example.excel.repository.FileRepository;

@Configuration
@EnableScheduling
public class FilesScheduler {

	@Autowired
	private FileRepository fileRepository;

	@Value("${excel.file.path}")
	private String excelFilePath;

	static final Logger LOGGER = LogManager.getLogger(FilesScheduler.class);

	@Scheduled(fixedRate = 30000) // Run every 30 seconds
	public void fileUploadation() throws FileNotFoundException, IOException {
		LOGGER.info("Scheduler Started for file upload ");
		List<ExcelFileEntity> data = fileRepository.findByDeletedFalseAndStatusSaved("saved");
		for (ExcelFileEntity entity : data) {
			File uploadFile = new File(excelFilePath + entity.getFileName());
			LOGGER.info("Iterating File one by one " + entity.getFileName());
			if (uploadFile.exists()) {
				uploadFile(uploadFile, entity);
			}
		}
	}

	public void uploadFile(File uploadFile, ExcelFileEntity entity) throws IOException {

		Workbook workbook = null;
		String fileName = entity.getFileName();
		String extension[] = fileName.split(Pattern.quote("."));
		if (extension[1].equals("xls")) {
			workbook = new HSSFWorkbook(new FileInputStream(uploadFile));
		} else {
			workbook = new XSSFWorkbook(new FileInputStream(uploadFile));
		}
		// Getting the Sheet
		Sheet sheet = workbook.getSheetAt(0);
		// Create a DataFormatter to format and get each cell's value as String
		DataFormatter dataFormatter = new DataFormatter();
		// 1. You can obtain a rowIterator and columnIterator and iterate over them
		Iterator<Row> rowIterator = sheet.rowIterator();

		int count = 0;
		List<FileDataEntity> fileData = new ArrayList<>();

		LOGGER.info("Excel File Uploading Process is in progress");
		entity.setStatus("upload is in progress");
		entity = fileRepository.save(entity);

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
				entity.setColumnName(data.toString().substring(0, data.length() - 1));
				count++;
			} else if (data.length() > 0) {
				FileDataEntity row_data = new FileDataEntity();
				row_data.setData(data.toString().substring(0, data.length() - 1));
				fileData.add(row_data);
			}

		}
		entity.setFileData(fileData);
		entity.setStatus("upload successfully");
		entity = fileRepository.save(entity);
		workbook.close();
		LOGGER.info("Excel File Uploading process Completed Successfully");
	}
}
