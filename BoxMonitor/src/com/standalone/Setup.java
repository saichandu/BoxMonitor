package com.standalone;

import java.io.IOException;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Setup {

	public static void main(String[] args) throws IOException {
		MongoClient client  = new MongoClient("localhost", 27017);
		MongoDatabase mdb = client.getDatabase("boxmonitor");
		
		MongoCollection<Document> coll = mdb.getCollection("booking");
		
		//mdb.createCollection("boxes");
		/*Document doc1 = new Document();
		doc1.put("_id", "USINDROPEACOCK6");
		doc1.put("box_name", "USINDROPEACOCK6");
		doc1.put("box_ip", "");
		doc1.put("owner", "FO");
		
		Document doc2 = new Document();
		doc2.put("_id", "USINDROPEACOCK9");
		doc2.put("box_name", "USINDROPEACOCK9");
		doc2.put("box_ip", "");
		doc2.put("owner", "Support");
		
		coll.insertOne(doc1);
		coll.insertOne(doc2);*/
		
		client.close();
	}
	
	//db.createCollection("users");
	//db.users.createIndex({"email":1}, {unique:true});
	//db.booking.createIndex({"email":1, "box_name":1}, {unique:true});
}
