package com.spice.email.app;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.mail.Session;
import javax.mail.Transport;

import org.apache.log4j.Logger;

import java.util.concurrent.CountDownLatch;

public class CampaignApp implements Runnable {
	String mailId = null;
	int campaign_id = 0;
	int linecount;
	String status = null;
	static Connection con = null;
	String emailContent = "";
	String senderMail = "";
	String mailSubject = "";
	String language = "";
	String displayName = "";
	String userId = "";
	String templatePath = "";
	String departmentName = "";
	Session session = null;
	Transport transport = null;
	CountDownLatch latch = null;
	public static org.apache.log4j.Logger log = Logger
			.getLogger(CampaignApp.class);
	static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	public CampaignApp(CountDownLatch latch, String mailId, int campaign_id,
			int linecount, String emailContent, String senderMail,
			String mailSubject, String language, String displayName,
			String userId, String templatePath, Session session,
			Transport transport, String departmentName) {
		this.latch = latch;
		this.mailId = mailId;
		this.campaign_id = campaign_id;
		this.linecount = linecount;
		this.emailContent = emailContent;
		this.senderMail = senderMail;
		this.mailSubject = mailSubject;
		this.language = language;
		this.displayName = displayName;
		this.userId = userId;
		this.session = session;
		this.transport = transport;
		this.templatePath = templatePath;
		this.departmentName = departmentName;
	}

	public void run() {
		jobExecution();
		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			log.error("Exception in sleep ::" + e);
		}

		latch.countDown();

	}

	public void jobExecution() {
		long systeim = System.currentTimeMillis();

		log.info(Thread.currentThread().getName()
				+ " Mail Sending::Campaign ID=" + campaign_id + ",senderMail="
				+ senderMail + ",mailId=" + mailId + ",emailContent="
				+ emailContent + ",mailSubject=" + mailSubject + ",language="
				+ language + ",displayName=" + displayName + ",userId="
				+ userId + ",session=" + session + ",transport=" + transport
				+ ",templatePath=" + templatePath + ",departmentName="
				+ departmentName + ",line=" + linecount);
		try {
			int ret = SendMail.sendEmail(session, transport, senderMail,
					displayName, mailId, "", emailContent, mailSubject, ""
							+ campaign_id, "" + userId, language, templatePath,
					departmentName,linecount);

			log.info(Thread.currentThread().getName()
					+ " Mail Sent::Campaign ID=" + campaign_id + ",senderMail="
					+ senderMail + ",mailId=" + mailId + ",emailContent="
					+ emailContent + ",mailSubject=" + mailSubject + ",line="
					+ linecount + ", resp=" + ret + ",time="
					+ (System.currentTimeMillis() - systeim));

		} catch (Exception e) {
			log.error("Exception in jobExecution()::Campaign ID=" + campaign_id
					+ ",senderMail=" + senderMail + ",mailId=" + mailId
					+ ",emailContent=" + emailContent + ",mailSubject="
					+ mailSubject + ",language=" + language + ",displayName="
					+ displayName + ",userId=" + userId + ",session=" + session
					+ ",transport=" + transport + ",templatePath="
					+ templatePath + ",departmentName=" + departmentName
					+ ",exception=" + e.getMessage());
			e.printStackTrace();
		}

	}
}
