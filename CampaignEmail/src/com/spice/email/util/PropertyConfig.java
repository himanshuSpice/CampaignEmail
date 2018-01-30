package com.spice.email.util;

import java.io.FileInputStream;
import java.util.Properties;

import lombok.Data;

import org.apache.log4j.PropertyConfigurator;

@Data
public class PropertyConfig {
	private String dataBaseIP;
	private String userName;
	private String passWord;
	private String dataBase;
	private String logPath;
	private String sleepTime;
	private String table_name;
	private String urlTable;
	private String host;
	private String email_port;
	private int nPoolSize;
	private String unsubURL;

/*	public String getDataBaseIP() {
		return dataBaseIP;
	}

	public void setDataBaseIP(String dataBaseIP) {
		this.dataBaseIP = dataBaseIP;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getDataBase() {
		return dataBase;
	}

	public void setDataBase(String dataBase) {
		this.dataBase = dataBase;
	}

	public String getLogPath() {
		return logPath;
	}

	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}

	public String getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(String sleepTime) {
		this.sleepTime = sleepTime;
	}

	public String getTable_name() {
		return table_name;
	}

	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}

	public String getUrlTable() {
		return urlTable;
	}

	public void setUrlTable(String urlTable) {
		this.urlTable = urlTable;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getEmail_port() {
		return email_port;
	}

	public void setEmail_port(String email_port) {
		this.email_port = email_port;
	}

	public int getnPoolSize() {
		return nPoolSize;
	}

	public void setnPoolSize(int nPoolSize) {
		this.nPoolSize = nPoolSize;
	}

	public String getUnsubURL() {
		return unsubURL;
	}

	public void setUnsubURL(String unsubURL) {
		this.unsubURL = unsubURL;
	}
	

	private PropertyConfig() {
		//PropertyConfigurator.configure("/home/campgui/campaignEmail/Jar/log4j.properties"); //For creating executable jar(export) to find logger path
	}*/
	public static PropertyConfig singleTon = null;
	public static PropertyConfig getSingleTonObject() {
		if (singleTon == null) {
			singleTon = new PropertyConfig();
			FileInputStream file = null;
			Properties properties1 = new Properties();
			try {
				if (OSValidator.isWindows()) {
					file = new FileInputStream("D:\\eclipse_luna\\CampaignEmail_StopResume\\src\\configFile.cfg");
				}else{
					file = new FileInputStream("/home/campgui/campaignEmail/configFile.cfg");
				}	
				properties1.load(file);

				//properties1.load(PropertyConfig.class.getClassLoader().getResourceAsStream("configFile.cfg"));//Find config in any location
				
				singleTon.setDataBaseIP(properties1.getProperty("db_ip"));
				singleTon.setUserName(properties1.getProperty("db_user"));
				singleTon.setPassWord(properties1.getProperty("db_password"));
				singleTon.setDataBase(properties1.getProperty("db_name"));
				singleTon.setLogPath(properties1.getProperty("log_path"));
				singleTon.setSleepTime(properties1.getProperty("sleep_time"));
				singleTon.setTable_name(properties1.getProperty("table_name"));
				singleTon.setUrlTable(properties1.getProperty("table_url"));
				singleTon.setHost(properties1.getProperty("mail.smtp.host"));
				singleTon.setEmail_port(properties1.getProperty("mail.smtp.port"));
				singleTon.setNPoolSize(Integer.parseInt(properties1.getProperty("pool_size")));
				singleTon.setUnsubURL(properties1.getProperty("unsubURL"));
				
			} catch (Exception ioe) {
				ioe.printStackTrace();
			}
		}
		return singleTon;
	}
}