package com.example.excel.domain;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author BT
 *
 */
@Entity
@Table(name = "excelFile")
public class ExcelFileEntity implements Cloneable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String fileName;

	private LocalDateTime lastAccess;

	@ManyToOne(fetch = FetchType.EAGER)
	private User user;

	private String columnName;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "excel_fileData", joinColumns = @JoinColumn(name = "excelFile_id"), inverseJoinColumns = @JoinColumn(name = "fileData_id"))
	private List<FileDataEntity> fileData;

	private boolean isDeleted;

	private String status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public LocalDateTime getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(LocalDateTime lastAccess) {
		this.lastAccess = lastAccess;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public List<FileDataEntity> getFileData() {
		return fileData;
	}

	public void setFileData(List<FileDataEntity> fileData) {
		this.fileData = fileData;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		// Shallow copy using super.clone()
		ExcelFileEntity clone = (ExcelFileEntity) super.clone();

		return clone;
	}
}
