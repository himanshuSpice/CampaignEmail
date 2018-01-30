package com.spice.email.dao;

import lombok.Data;

@Data
public class EmailDaoPojo {
	private int campaignId;
	private String campaignName;
	private String absolutePath;
	private String ueserId;
	private String message;
	private String htmlTemplate;
	private String senderEmailIid;
	private String subject;
	private String language;
	private String dispalyName;
	private String templatePath;
	private String departmentName;
	private int processedCount;
}
