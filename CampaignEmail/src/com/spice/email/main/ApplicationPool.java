package com.spice.email.main;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import org.apache.commons.pool.ObjectPool;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.spice.email.util.*;
import com.spice.email.dao.*;
import com.spice.email.app.StatusCheckDb;

public class ApplicationPool {
	public static ObjectPool pool = null;
	static Connection con = null;
	static int size = 0;
	public static org.apache.log4j.Logger log = Logger
			.getLogger(ApplicationPool.class);
	static ExecutorService executorService = null;
	static boolean exit_flag = false;
	static public HashMap<Integer, String> runningCampaign = new HashMap<Integer, String>();
	static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	
	public static void fetchCampaign(String code) {
		int campaign_id = 0;
		String campaign_name = "";
		String path = "";
		String userId = "";
		String emailContent = "";
		String htmlTemplate = "";
		String sender = "";
		String mailSubject = "";
		String language = "";
		String displayName = "";
		String templatePath = "";
		String departmentName = "";
		int processedCount = 0;

		ArrayList<EmailDaoPojo> emaildao = EmailDao.campaignSelect(code);
		System.out.println("Total CampaignIds selected from db =" + emaildao.size());
		if (emaildao.size() > 0) {
			for (int i = 0; i < emaildao.size(); i++) {
				campaign_id = emaildao.get(i).getCampaignId();
				campaign_name = emaildao.get(i).getCampaignName();
				path = emaildao.get(i).getAbsolutePath();
				userId = emaildao.get(i).getUeserId();
				emailContent = emaildao.get(i).getMessage();
				htmlTemplate = emaildao.get(i).getHtmlTemplate();
				sender = emaildao.get(i).getSenderEmailIid();
				mailSubject = emaildao.get(i).getSubject();
				language = emaildao.get(i).getLanguage();
				displayName = emaildao.get(i).getDispalyName();
				templatePath = emaildao.get(i).getTemplatePath();
				departmentName = emaildao.get(i).getDepartmentName();
				processedCount = emaildao.get(i).getProcessedCount();
				runningCampaign.put(campaign_id, "true");
				log.info("Selected Campaign [" + campaign_id + "],"
						+ "campaign_name=" + campaign_name + ",path=" + path
						+ ",userId=" + userId + ",emailContent=" + emailContent
						+ ",htmlTemplate=" + htmlTemplate + ",sender=" + sender
						+ ",mailSubject=" + mailSubject + ",language="
						+ language + ",displayName=" + displayName
						+ ",templatePath=" + templatePath + ",departmentName="
						+ departmentName+",processed_count="+processedCount);

				CampaignSelect campSelect = new CampaignSelect(campaign_id, campaign_name, path, userId, emailContent, htmlTemplate, sender, mailSubject, language, displayName, templatePath, departmentName,processedCount);
				Thread t1 = new Thread(campSelect);
				t1.start();
			}
		} else {
			System.out.println("Campaign Id not available");
		}
	}
	
	public static void main(String[] args) {
		try {
			PropertyConfig propertyConfig = PropertyConfig.getSingleTonObject();
			pool = DbPool.initMySqlConnectionPool();
			executorService = Executors.newFixedThreadPool(propertyConfig
					.getNPoolSize());

			StatusCheckDb statuscheckdb = new StatusCheckDb();
			Thread t = new Thread(statuscheckdb);
			t.start();

			while (!exit_flag) {
				fetchCampaign("1");
				Thread.sleep(Integer.parseInt(propertyConfig.getSleepTime()));				
			}
		} catch (Exception e) {
			log.error("Exception in Main() ::" + e.getMessage());
			e.printStackTrace();
		}
		executorService.shutdown();
	}
}
