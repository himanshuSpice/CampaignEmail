package com.spice.email.util;

import java.util.Properties;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPoolFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool.Config;

public class DbPool {
	public static ObjectPool initMySqlConnectionPool() {
		//Properties properties = new Properties();
		PropertyConfig propertyConfig=	PropertyConfig.getSingleTonObject();
		ObjectPool pool = null;
		try {
					
			String host = propertyConfig.getDataBaseIP();
			String schema = propertyConfig.getDataBase();
			String user = propertyConfig.getUserName();
			String password = propertyConfig.getPassWord();

			PoolableObjectFactory mySqlPoolableObjectFactory = new MySqlPoolableObjectFactory(host, schema, user, password);
			org.apache.commons.pool.impl.GenericObjectPool.Config config = new GenericObjectPool.Config();
			config.maxActive = 20;

			config.testOnBorrow = true;
			config.testWhileIdle = true;
			config.timeBetweenEvictionRunsMillis = 10000;
			config.minEvictableIdleTimeMillis = 60000;

			GenericObjectPoolFactory genericObjectPoolFactory = new GenericObjectPoolFactory(mySqlPoolableObjectFactory,
					config);
			pool = genericObjectPoolFactory.createPool();
		} catch (Exception e) {

			e.printStackTrace();
		}
		return pool;
	}

}
