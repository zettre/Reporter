package com.tcs.reporter.bean;

public class Config {
	private String projectRootFolder;
	private String pdfSavePath;
	private String reportsFileName;
	private String reportsFilePath;
	private String logFilePath;
	private String dbUrl;
	private String driver;
	private String username;
	private String password;
	public String getProjectRootFolder() {
		return projectRootFolder;
	}
	public void setProjectRootFolder(String projectRootFolder) {
		this.projectRootFolder = projectRootFolder;
	}
	public String getPdfSavePath() {
		return pdfSavePath;
	}
	public void setPdfSavePath(String pdfSavePath) {
		this.pdfSavePath = pdfSavePath;
	}
	public String getReportsFileName() {
		return reportsFileName;
	}
	public void setReportsFileName(String reportsFileName) {
		this.reportsFileName = reportsFileName;
	}
	public String getReportsFilePath() {
		return reportsFilePath;
	}
	public void setReportsFilePath(String reportsFilePath) {
		this.reportsFilePath = reportsFilePath;
	}
	public String getLogFilePath() {
		return logFilePath;
	}
	public void setLogFilePath(String logFilePath) {
		this.logFilePath = logFilePath;
	}
	public String getDbUrl() {
		return dbUrl;
	}
	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
