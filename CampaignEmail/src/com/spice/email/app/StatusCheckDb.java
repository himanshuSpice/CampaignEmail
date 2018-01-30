
package com.spice.email.app;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.pool.ObjectPool;
import org.apache.log4j.Logger;

import com.spice.email.dao.EmailDao;
import com.spice.email.dao.EmailDaoPojo;
import com.spice.email.main.*;
import com.spice.email.util.PropertyConfig;

public class StatusCheckDb implements Runnable {
	static ObjectPool pool = null;
	static Connection con = null;
	public static org.apache.log4j.Logger log = Logger.getLogger(StatusCheckDb.class);
	public static Statement stmtSelect = null;
	public static String query = "";
	public static ResultSet rs = null;
	public static int campaignId = 0;

	public void run() {
		
		while (true) {
			System.out.println("StatusCheckDb"+Thread.currentThread().getName() + ""+ " START");
			stopped();
			resume();
			System.out.println("StatusCheckDb"+Thread.currentThread().getName() + ""+ " END ");
		}
	}
	public void stopped(){
		PropertyConfig propertyConfig = PropertyConfig.getSingleTonObject();
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			con = (Connection) ApplicationPool.pool.borrowObject();
			ArrayList<EmailDaoPojo> emaildao = EmailDao.campaignSelect2("0");
			if (emaildao.size() > 0) {
				for (int i = 0; i < emaildao.size(); i++) {
					campaignId = emaildao.get(i).getCampaignId();
					log.info(Thread.currentThread().getName() +" Cancelling the Campaign Id if it is running="+campaignId);
					if(ApplicationPool.runningCampaign.containsKey(campaignId)){
						ApplicationPool.runningCampaign.put(campaignId, "false");
					
						log.info(Thread.currentThread().getName() +" Cancelled Campaign ID" + campaignId + ", date time "+ dateFormat.format(date));
					}
				}
			}

			Thread.sleep(Integer.parseInt(propertyConfig.getSleepTime()));

		} catch (Exception e) {
			log.error("Exception in run() ::" + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				ApplicationPool.pool.returnObject(con);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void resume(){
		PropertyConfig propertyConfig = PropertyConfig.getSingleTonObject();
		try {
		
			con = (Connection) ApplicationPool.pool.borrowObject();
			
			ApplicationPool.fetchCampaign("2");
			//ArrayList<EmailDaoPojo> emaildao = EmailDao.campaignSelect2("2");
			

			Thread.sleep(Integer.parseInt(propertyConfig.getSleepTime()));

		} catch (Exception e) {
			log.error("Exception in run() ::" + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				ApplicationPool.pool.returnObject(con);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
