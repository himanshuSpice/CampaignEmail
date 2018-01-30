package com.spice.email.util;

import java.sql.DriverManager;

import org.apache.commons.pool.BasePoolableObjectFactory;

public class MySqlPoolableObjectFactory extends BasePoolableObjectFactory{
	 private String ip;
     
     private String schema;
     private String user;
     private String password;
 
     public MySqlPoolableObjectFactory(String db_ip, String schema,String user, String password) {
          this.ip = db_ip;
          this.schema = schema;
          this.user = user;
          this.password = password;
     }
 
     @Override
     public Object makeObject() throws Exception {
          Class.forName("com.mysql.jdbc.Driver").newInstance();
          String url = "jdbc:mysql://" +ip+ "/"
               + schema + "?autoReconnectForPools=true";
          return DriverManager.getConnection(url, user, password);
      }

}
