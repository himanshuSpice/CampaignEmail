package com.spice.email.main;

import java.io.BufferedReader;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.spice.email.app.*;
import com.spice.email.util.PropertyConfig;

import java.util.concurrent.*;

public class CampaignSelect implements Runnable {
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
	Session session = null;
	Transport transport = null;
	CountDownLatch latch = null;

	public static org.apache.log4j.Logger log = Logger
			.getLogger(CampaignSelect.class);

	CampaignSelect(int campaign_id, String campaign_name, String path,
			String userId, String emailContent, String htmlTemplate,
			String sender, String mailSubject, String language,
			String displayName, String templatePath, String departmentName,
			int processedCount) {
		this.campaign_id = campaign_id;
		this.campaign_name = campaign_name;
		this.path = path;
		this.userId = userId;
		this.emailContent = emailContent;
		this.htmlTemplate = htmlTemplate;
		this.sender = sender;
		this.mailSubject = mailSubject;
		this.language = language;
		this.displayName = displayName;
		this.templatePath = templatePath;
		this.departmentName = departmentName;
		this.processedCount = processedCount;
	}

	public void run() {
		PropertyConfig propertyConfig = PropertyConfig.getSingleTonObject();
		long time = System.currentTimeMillis();

		log.info("Campaign ID= " + campaign_id + ",campaign_name="
				+ campaign_name + ",path=" + path + ",userId=" + userId
				+ ",sender=" + sender + ",emailContent=" + emailContent
				+ ",htmlTemplate=" + htmlTemplate + ",mailSubject="
				+ mailSubject + ",language=" + language + ",displayName="
				+ displayName + ",tempalte path=" + templatePath + ",deptName="
				+ departmentName + ",processedCount=" + processedCount);

		session = SendMail.getSession(sender, "sample@1234");
		transport = SendMail
				.getSessionTransport(session, sender, "sample@1234");
		try {

			final Path p = Paths.get(path);
			long FileLineCount = Files.lines(p).count() - processedCount;
			latch = new CountDownLatch((int) FileLineCount);
			System.out.println("latch size(File Line Count)=" + latch.getCount());		
			
			FileReader fr = new FileReader(path);
			BufferedReader reader = new BufferedReader(fr);

			String line;
			int linecount = processedCount + 1;
			int count = 0;
			while ((line = reader.readLine()) != null) {
				count++;
				if (count <= processedCount) {
					System.out.println("count=" + count + ",processed="
							+ processedCount);
				} else {
					if (isTrue(line.trim())) {

						if (ApplicationPool.runningCampaign.get(campaign_id)
								.toString().equals("true")) {

							Runnable worker = new CampaignApp(latch, line,
									campaign_id, linecount, emailContent,
									sender, mailSubject, language, displayName,
									userId, templatePath, session, transport,
									departmentName);
							ApplicationPool.executorService.execute(worker);

							Thread.sleep(2000);
						} else {
							latch = new CountDownLatch(0);
							break;
						}
					}

					log.info("Campaign ID =" + campaign_id + ",campaign_name="
							+ campaign_name + ",Camp Id status " + linecount
							+ ApplicationPool.runningCampaign.get(campaign_id));
					linecount++;

				}
			}
			reader.close();
			try {
				System.out
						.println("we are in waiting  latch.await() latch count="
								+ latch.getCount());
				latch.await();
				System.out.println("wait is over latch count = "
						+ latch.getCount());
			} catch (InterruptedException ie) {
				System.out.println("Exception in latch wait ::" + ie);
				ie.printStackTrace();
			}

			log.info("Campaign ID=" + campaign_id + ",Campaign_name="
					+ campaign_name + ",File Name=" + path + "Resp time="
					+ (System.currentTimeMillis() - time));

		} catch (Exception exp) {
			log.error("Exception in Reading file or Executor :"
					+ exp.getMessage());
			exp.printStackTrace();
		} finally {
			try {
				transport.close();

			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("Campaign End:: Campaign ID " + campaign_id
					+ ",Campaign name=" + campaign_name
					+ " Executor1 shutdown and Transport session closed");

		}
	}

	public static boolean isTrue(String s) {
		boolean ret = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{1,5}$")
				.matcher(s).matches();
		return ret;
	}
}
