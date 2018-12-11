package com.tcs.reporter.bean;

public class Report {
	private String name;
	private String serialNumber;
	private String sqlFile;
	private String columnMappingFile;
	private String pdfPropertiesFile;
	private String argumentsFile;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getSqlFile() {
		return sqlFile;
	}
	public void setSqlFile(String sqlFile) {
		this.sqlFile = sqlFile;
	}
	public String getColumnMappingFile() {
		return columnMappingFile;
	}
	public void setColumnMappingFile(String columnMappingFile) {
		this.columnMappingFile = columnMappingFile;
	}
	public String getPdfPropertiesFile() {
		return pdfPropertiesFile;
	}
	public void setPdfPropertiesFile(String pdfPropertiesFile) {
		this.pdfPropertiesFile = pdfPropertiesFile;
	}
	public String getArgumentsFile() {
		return argumentsFile;
	}
	public void setArgumentsFile(String argumentsFile) {
		this.argumentsFile = argumentsFile;
	}
	
}
