package com.mongo;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.constants.ApplicationConstants;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.util.PropertiesUtil;

public class MongoDBConnManager {
	private static volatile MongoDBConnManager instance;
	private static String DEFAULT_SCHEMA;
	private static MongoClient mongoClient;
	private static MongoDatabase database;

	private MongoDBConnManager() {
	}

	public static MongoDBConnManager getInstance() {
		if (instance == null) {
			synchronized (MongoDBConnManager.class) {
				if (instance == null) {
					instance = new MongoDBConnManager();
					final Properties props = PropertiesUtil.getInstance()
							.getProps();
					mongoClient = new MongoClient(
							props.getProperty(ApplicationConstants.MONGO_DB_HOST),
							Integer.parseInt(props
									.getProperty(ApplicationConstants.MONGO_DB_PORT)));
					DEFAULT_SCHEMA = props
							.getProperty(ApplicationConstants.DEFAULT_SCHEMA);
				}
			}
		}
		return instance;
	}

	public MongoDatabase getConnection(String dbname) {
		if (StringUtils.equalsIgnoreCase(DEFAULT_SCHEMA, dbname)) {
			if (database == null) {
				database = mongoClient.getDatabase(dbname);
			}
		} else {
			database = mongoClient.getDatabase(dbname);
		}
		return database;
	}

	public MongoDatabase getConnection() {
		return getConnection(DEFAULT_SCHEMA);
	}

	public void closeMongoClient() {
		if (mongoClient != null) {
			mongoClient.close();
		}
	}
}