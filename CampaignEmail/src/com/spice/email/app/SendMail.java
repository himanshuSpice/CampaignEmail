package com.spice.email.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import javax.mail.BodyPart;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

import com.spice.email.dao.EmailDao;
import com.spice.email.util.OSValidator;
import com.spice.email.util.PropertyConfig;

import java.net.*;

public class SendMail {
	public static org.apache.log4j.Logger log = Logger
			.getLogger(SendMail.class);

	// static Transport transport = null;
	public static String statusDescription = null;
	static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	public static Session getSession(String username, String password) {
		Session session = null;
		PropertyConfig congir = PropertyConfig.getSingleTonObject();
		if (session == null) {
			if (OSValidator.isWindows()) {
				Properties props = new Properties();
				props.put("mail.smtp.auth", "true"); // false in production
				props.put("mail.smtp.starttls.enable", "true");
				props.put("mail.smtp.host", congir.getHost());
				props.put("mail.smtp.port", congir.getEmail_port());
				//session = Session.getDefaultInstance(props);//uncomment in production
	
				/* for testing in local */
				
				session = Session.getInstance(props, new
				  javax.mail.Authenticator() { protected PasswordAuthentication
				  getPasswordAuthentication() { return new
				  PasswordAuthentication(username, password); } });
			}else{
				Properties props = new Properties();
				props.put("mail.smtp.auth", "false"); // false in production
				props.put("mail.smtp.starttls.enable", "true");
				props.put("mail.smtp.host", congir.getHost());
				props.put("mail.smtp.port", congir.getEmail_port());
				session = Session.getDefaultInstance(props);//uncomment in production	
			} 

		}
		return session;
	}

	public static Transport getSessionTransport(Session session,
			String username, String password) {
		Transport transport = null;
		try {
			transport = session.getTransport("smtp");
			transport.connect(username, password);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return transport;
	}

	public static int sendEmail(Session session, Transport transport,
			String sender, String senderName, String reciever,
			String receivername, String emailContent, String subject,
			String campId, String userId, String language, String templatePath,
			String departmentName,int linecount) {
		PropertyConfig propertyConfig = PropertyConfig.getSingleTonObject();
		int ret = 0;// 0 means success
		int processCount = linecount;
		Date date = null;
		date = new Date();
		if (language.equals("2")) {
			emailContent = EncriptDecript.getMessage(emailContent);
			System.out.println("campId="+campId+",After UNICODE emailContent=" + emailContent);
						
		}
		String unsubURL = "http://" + propertyConfig.getUnsubURL();

		try {
			MimeMessage message = new MimeMessage(session);
			
			
			message.setSubject(subject);
	
			InternetAddress from = new InternetAddress(sender, senderName);
			message.setFrom(from);
			InternetAddress to = new InternetAddress(reciever, receivername);
			message.setRecipient(Message.RecipientType.TO, to);

			String encriptString = EncriptDecript.encrypt(reciever,
					departmentName);

			unsubURL = unsubURL.replace("$1", reciever);
			unsubURL = unsubURL.replace("$2", departmentName);
			
			//http://stgreporting.umang.gov.in/unsub/index?emailID=himanshu.sharma@spicedigital.in&department=TESTING

			log.info("Campaign ID=" + campId +",sender=" + sender + ",senderName=" + senderName
					+ ",reciever=" + reciever + ",receivername=" + receivername
					+ ",emailContent=" + emailContent + ",subject=" + subject
					+ ",userId=" + userId + ",language="
					+ language + ",templatePath=" + templatePath
					+ ",departmentName=" + departmentName + ",unsubURL="
					+ unsubURL);

			// BodyPart body1 = new MimeBodyPart();
			//
			// // velocity stuff.
			//
			// // Initialize velocity
			// VelocityEngine ve = new VelocityEngine();
			// ve.init();
			//
			// /* next, get the Template */
			// Template t =
			// ve.getTemplate("D:\\Projects\\MailingRunnableThread2\\src\\mail.vm");
			// /* create a context and add data */
			// VelocityContext context = new VelocityContext();
			// context.put("User","himanshu Sharma");
			//
			// /* now render the template into a StringWriter */
			// StringWriter out = new StringWriter();
			// t.merge( context, out );
			//
			// // velocity stuff ends.
			//
			// body1.setContent(out.toString(), "text/html");
			//
			// Multipart multipart = new MimeMultipart();
			// multipart.addBodyPart(body1);
			//
			// message.setContent(multipart, "text/html");
			//
			
						
			MimeMultipart multipart = new MimeMultipart();
			
			BodyPart messageBodyPart = new MimeBodyPart();
			
			emailContent = emailContent.replace("^", "<br/>");

			// Set key values
			Map<String, String> input = new HashMap<String, String>();
			input.put("######Email######", emailContent);
			input.put("#####PARAMETER####", encriptString);
			
			// HTML mail content
			String htmlText = readEmailFromHtml(templatePath, input);
			//messageBodyPart.setContent(htmlText, "text/html");
			//System.out.println("htmlText="+htmlText);
			messageBodyPart.setContent(htmlText, "text/html; charset=utf-8");
						

			multipart.addBodyPart(messageBodyPart);
			message.setContent(multipart);
		
			//message.setHeader("List-Unsubscribe", "<himanshu.sharma@spicedigital.in>");
			message.addHeader("List-Unsubscribe", "<"+reciever+">, <"+unsubURL+">");
			//message.addHeader("List-Unsubscribe", "<himanshu.sharma@spicedigital.in>");
						
			transport.send(message, message.getAllRecipients());

			Enumeration headers = message.getAllHeaders();
            while (headers.hasMoreElements()) {
                Header header = (Header) headers.nextElement();
                System.out.println(header.getName() + ": " + header.getValue());
            }
						
			
			statusDescription = "Success";

		} catch (Exception e) {
			e.printStackTrace();
			statusDescription = e.getMessage();
			log.error("Exception in sendEmail():: " + e.getMessage());
			ret = 1; // 1 means Failed
		} finally {

			try {

				 EmailDao.emailInserProc(sender, reciever, emailContent,
				 emailContent.length(), 1, 1, dateFormat.format(date),
				 ret, statusDescription, campId, userId,processCount);

			} catch (Exception e) {

				e.printStackTrace();

			}
			log.info(",Campaign ID=" + campId +",sender=" + sender + ",senderName=" + senderName
					+ ",reciever=" + reciever + ",receivername=" + receivername
					+ ",emailContent=" + emailContent + ",subject=" + subject
					+  ",userId=" + userId + ",language="
					+ language + ",templatePath=" + templatePath
					+ ",departmentName=" + departmentName + ",unsubURL="
					+ unsubURL + ",ret=" + ret + ",statusDescription=["
					+ statusDescription+"]");

		}
		return ret;
	}

	// Method to replace the values for keys
	protected static String readEmailFromHtml(String filePath,
			Map<String, String> input) {
		String msg = readContentFromFile(filePath);
		try {
			Set<Entry<String, String>> entries = input.entrySet();
			for (Map.Entry<String, String> entry : entries) {
				msg = msg.replace(entry.getKey().trim(), entry.getValue()
						.trim());
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return msg;
	}

	// Method to read HTML file as a String
	private static String readContentFromFile(String fileName) {
		StringBuffer contents = new StringBuffer();

		try {
			// use buffering, reading one line at a time
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			try {
				String line = null;
				while ((line = reader.readLine()) != null) {
					contents.append(line);
					contents.append(System.getProperty("line.separator"));					
				}
			} finally {
				reader.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return contents.toString();
	}

	public static void main(String args[]) {
		// SendMail ob = new SendMail();
		Session session = getSession("sampletest2005@gmail.com", "sample@1234");
		Transport transport = getSessionTransport(session,
				"sampletest2005@gmail.com", "sample@1234");
		int linecount=1;
		int ret =sendEmail(session, transport, "sampletest2005@gmail.com", "himanshu",
				"himanshu.sharma@spicedigital.in", "receiver name", "",
				"subject", "1", "1", "2",
				"D:\\eclipse_project\\localCampaignEmail\\src\\umang.html",
				"departmentName",linecount);
		if(ret == 0){
			System.out.println("Mail sent successfully");
		}else{
			System.out.println("Mail sending Failed");
		}
	}
}
