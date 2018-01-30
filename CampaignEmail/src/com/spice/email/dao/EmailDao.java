//Himanshu Sharma
//spicedigital
package com.spice.email.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.spice.email.main.ApplicationPool;

public class EmailDao {

	public static org.apache.log4j.Logger log = Logger
			.getLogger(EmailDao.class);

	public static void emailInserProc(String senderEmail, String receiverMail,
			String mailContent, int mailContentLength, int errorCode,
			int errorType, String submitDate, int status,
			String statusDescription, String campaignId, String userId,int processedCount) {
		log.info("[Campaign Id="+campaignId+",User Id="+userId+",Sender=" + senderEmail + ",Receiver=" + receiverMail
				+ ",status=" + status + "]");
		Connection conn = null;

		try {
			conn = (Connection) ApplicationPool.pool.borrowObject();
			CallableStatement cStmt = null;

			cStmt = conn
					.prepareCall("{call smsEngine.proc_email_mis_insert(?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			cStmt.setString(1, senderEmail);
			cStmt.setString(2, receiverMail);
			cStmt.setString(3, mailContent);
			cStmt.setInt(4, mailContentLength);
			cStmt.setInt(5, errorCode);
			cStmt.setInt(6, errorType);
			cStmt.setDate(7, getCurrentDate());
			cStmt.setInt(8, status);
			cStmt.setString(9, statusDescription);
			cStmt.setInt(10, Integer.parseInt(campaignId));
			cStmt.setInt(11, Integer.parseInt(userId));
			cStmt.setInt(12, processedCount);
			cStmt.registerOutParameter(13, Types.VARCHAR);
			cStmt.registerOutParameter(14, Types.INTEGER);
			cStmt.registerOutParameter(15, Types.VARCHAR);
			boolean rs = cStmt.execute();

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				ApplicationPool.pool.returnObject(conn);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static ArrayList campaignSelect(String In_flag) {
		ArrayList<EmailDaoPojo> list = new ArrayList<EmailDaoPojo>();
		Connection conn = null;
		System.out
				.println("[CampaignSelect from procedure proc_campaign_select2 In_flag= "
						+ In_flag + "]");
		ResultSet rs = null;
		try {
			conn = (Connection) ApplicationPool.pool.borrowObject();
			CallableStatement cStmt = null;

			cStmt = conn.prepareCall("{call proc_campaign_select2(?,?,?,?)}");
			cStmt.setString(1, In_flag);
			cStmt.registerOutParameter(2, Types.VARCHAR);
			cStmt.registerOutParameter(3, Types.INTEGER);
			cStmt.registerOutParameter(4, Types.VARCHAR);

			boolean results = cStmt.execute();
			while (results) {
				rs = cStmt.getResultSet();
				String ret = cStmt.getString(2);
				if (!ret.equals("-1")) {
					if (rs != null) {
						while (rs.next()) {
							EmailDaoPojo emaildao = new EmailDaoPojo();
							emaildao.setCampaignId(rs.getInt("vcampaign_id"));
							emaildao.setCampaignName(rs
									.getString("vcampaign_name"));
							emaildao.setAbsolutePath(rs
									.getString("vsuccess_path"));
							emaildao.setUeserId(rs.getString("vuser_id"));
							emaildao.setMessage(rs.getString("vmessage"));
							emaildao.setHtmlTemplate(rs
									.getString("vhtmlTemplate"));
							emaildao.setSenderEmailIid(rs
									.getString("vsender_email_id"));
							emaildao.setSubject(rs.getString("vmail_subject"));
							emaildao.setLanguage(rs.getString("vmsg_id"));
							emaildao.setDispalyName(rs.getString("vdisplay"));
							emaildao.setTemplatePath(rs
									.getString("vtemplatepath"));
							emaildao.setDepartmentName(rs
									.getString("vdeptname"));
							emaildao.setProcessedCount(rs.getInt("vprocessed_count"));
							list.add(emaildao);
						}
						rs.close();
					}
				}
				results = cStmt.getMoreResults();
			}

		} catch (Exception e) {
			log.error("Exception in campaignSelect():: " + e.getMessage());
			e.printStackTrace();

		} finally {
			try {

				ApplicationPool.pool.returnObject(conn);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}

	public static ArrayList campaignSelect2(String In_flag) {
		ArrayList<EmailDaoPojo> list = new ArrayList<EmailDaoPojo>();

		Connection conn = null;
		System.out
				.println("[CampaignSelect2 from procedure proc_campaign_select2 In_flag= "
						+ In_flag + "]");
		ResultSet rs = null;
		try {
			conn = (Connection) ApplicationPool.pool.borrowObject();
			CallableStatement cStmt = null;

			cStmt = conn.prepareCall("{call proc_campaign_select2(?,?,?,?)}");
			cStmt.setString(1, In_flag);
			cStmt.registerOutParameter(2, Types.VARCHAR);
			cStmt.registerOutParameter(3, Types.INTEGER);
			cStmt.registerOutParameter(4, Types.VARCHAR);

			rs = cStmt.executeQuery();

			String ret = cStmt.getString(2);
			if (!ret.equals("-1")) {
				while (rs.next()) {
					EmailDaoPojo emaildao = new EmailDaoPojo();
					emaildao.setCampaignId(rs.getInt("campaign_id"));
					emaildao.setCampaignName(rs.getString("campaign_name"));
					emaildao.setAbsolutePath(rs.getString("success_path"));
					emaildao.setUeserId(rs.getString("user_id"));
					emaildao.setMessage(rs.getString("message"));
					emaildao.setHtmlTemplate(rs.getString("htmlTemplate"));
					emaildao.setSenderEmailIid(rs.getString("sender_email_id"));
					emaildao.setSubject(rs.getString("email_subject"));
					emaildao.setLanguage(rs.getString("message_type_id"));
					// emaildao.setSenderId(rs.getString("vsender_id"));

					list.add(emaildao);
				}
				rs.close();

			}

		} catch (Exception e) {
			log.error("Exception in campaignSelect()2:: " + e.getMessage());
			e.printStackTrace();

		} finally {
			try {

				ApplicationPool.pool.returnObject(conn);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}

	private static java.sql.Date getCurrentDate() {
		java.util.Date today = new java.util.Date();
		return new java.sql.Date(today.getTime());
	}
}
