package com.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.constants.DBConstants;
import com.exception.ApplicationException;
import com.exception.MessagesEnum;
import com.mongo.MongoDBConnManager;
import com.mongodb.Block;
import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vo.Booking;
import com.vo.User;

public class DataAccess {

	private static volatile DataAccess instance;

	private DataAccess() {
	}

	public static DataAccess getInstance() {
		if (instance == null) {
			synchronized (DataAccess.class) {
				if (instance == null) {
					instance = new DataAccess();
				}
			}
		}
		return instance;
	}

	public Map<String, List<String>> getBoxes() {
		final Map<String, List<String>> boxesMap = new LinkedHashMap<String, List<String>>();
		final MongoDatabase mdb = MongoDBConnManager.getInstance().getConnection();
		final MongoCollection<Document> coll = mdb.getCollection(DBConstants.COLL_BOXES);
		final Document sortCr = new Document();
		sortCr.put(DBConstants.OWNER, 1);
		final FindIterable<Document> cursor = coll.find().sort(sortCr);
		cursor.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				List<String> boxes;
				if (!boxesMap.containsKey(document.getString(DBConstants.OWNER))) {
					boxes = new ArrayList<String>();
					boxes.add(document.getString(DBConstants.BOX_NAME));
				} else {
					boxes = boxesMap.get(document.get(DBConstants.OWNER)
							.toString());
					boxes.add(document.getString(DBConstants.BOX_NAME));
				}
				boxesMap.put(document.getString(DBConstants.OWNER), boxes);
			}
		});
		return boxesMap;
	}
	
	public boolean addUser(final Map<String, String> userInfo) {
		try {
			final MongoDatabase mdb = MongoDBConnManager.getInstance().getConnection();
			final MongoCollection<Document> coll = mdb.getCollection(DBConstants.COLL_USERS);
			final Document user = new Document();
			user.putAll(userInfo);
			coll.insertOne(user);
		} catch (MongoWriteException e) {
			if (e.getCode() == 11000) {
				throw new ApplicationException(MessagesEnum.DUPLICATE_USER.getMessage(userInfo.get(DBConstants.EMAIL)), e);
			}
		} catch (Exception e) {
			if (e instanceof com.mongodb.MongoTimeoutException) {
				throw new ApplicationException(MessagesEnum.MONGODB_IS_DOWN.getMessage(), e);
			}
			throw new ApplicationException(MessagesEnum.ADD_USER_FAILED.getMessage(), e);
		}
		return true;
	}
	
	public String getUserNameWithEmail(final String email) {
		try {
			final MongoDatabase mdb = MongoDBConnManager.getInstance().getConnection();
			final MongoCollection<Document> coll = mdb.getCollection(DBConstants.COLL_USERS);
			final Document bson = new Document();
			bson.put(DBConstants.EMAIL, email);
			final List<Document> users = coll.find(bson).into(new ArrayList<Document>());
			if (users == null || (users != null && users.size() == 0)) {
				return "";
			} else {
				//'0' index is accessed because email is uniquely maintained at DB level
				return users.get(0).getString(DBConstants.USER_NAME);
			}
		} catch (Exception e) {
			if (e instanceof com.mongodb.MongoTimeoutException) {
				throw new ApplicationException(MessagesEnum.MONGODB_IS_DOWN.getMessage(), e);
			}
			throw new ApplicationException(MessagesEnum.RETRIVAL_FAILED.getMessage(email), e);
		}
	}
	
	public String getBoxOwner(final String boxName) {
		try {
			final MongoDatabase mdb = MongoDBConnManager.getInstance().getConnection();
			final MongoCollection<Document> coll = mdb.getCollection(DBConstants.COLL_BOXES);
			final Document bson = new Document();
			bson.put(DBConstants.BOX_NAME, boxName);
			final List<Document> boxes = coll.find(bson).into(new ArrayList<Document>());
			if (boxes == null || (boxes != null && boxes.size() == 0)) {
				return "";
			} else {
				//'0' index is accessed because boxes are uniquely maintained at DB level
				return boxes.get(0).getString(DBConstants.OWNER);
			}
		} catch (Exception e) {
			if (e instanceof com.mongodb.MongoTimeoutException) {
				throw new ApplicationException(MessagesEnum.MONGODB_IS_DOWN.getMessage(), e);
			}
			throw new ApplicationException(MessagesEnum.RETRIVAL_FAILED.getMessage(), e);
		}
	}

	public boolean saveBooking(final Map<String, Object> bookingInfo) {
		try {
			final MongoDatabase mdb = MongoDBConnManager.getInstance().getConnection();
			final MongoCollection<Document> coll = mdb.getCollection(DBConstants.COLL_BOOKING);
			final Document booking = new Document();
			booking.putAll(bookingInfo);
			coll.insertOne(booking);
		} catch (MongoWriteException e) {
			if (e.getCode() == 11000) {
				throw new ApplicationException(
						MessagesEnum.BOOKINGS_ALREADY_EXISTS.getMessage(
								bookingInfo.get(DBConstants.BOX_NAME),
								bookingInfo.get(DBConstants.EMAIL)), e);
			}
		} catch (Exception e) {
			if (e instanceof com.mongodb.MongoTimeoutException) {
				throw new ApplicationException(MessagesEnum.MONGODB_IS_DOWN.getMessage(), e);
			}
			throw new ApplicationException(MessagesEnum.BOOKING_FAILED.getMessage(), e);
		}
		return true;
	}
	
	public Map<String, Booking> getAllBookings() {
		final Map<String, Booking> bookings =  new LinkedHashMap<String, Booking>();
		try {
			final MongoDatabase mdb = MongoDBConnManager.getInstance().getConnection();
			final MongoCollection<Document> coll = mdb.getCollection(DBConstants.COLL_BOOKING);
			List<Document> aggregates = new ArrayList<Document>();
			
			Document join = new Document();
			Document sortCr = new Document();
			
			/*Document lookup = new Document();
			lookup.put("from", DBConstants.COLL_BOXES);
			lookup.put("localField", DBConstants.BOX_NAME);
			lookup.put("foreignField", DBConstants.BOX_NAME);
			lookup.put("as", "join_for_teamname");*/
			
			Document lookup2 = new Document();
			lookup2.put("from", DBConstants.COLL_USERS);
			lookup2.put("localField", DBConstants.EMAIL);
			lookup2.put("foreignField", DBConstants.EMAIL);
			lookup2.put("as", "join_for_username");
			
			//join.put("$lookup", lookup);
			join.put("$lookup", lookup2);
			sortCr.put("$sort", new Document(DBConstants.TEAM_NAME, 1));
			
			aggregates.add(join);
			aggregates.add(sortCr);
			
			final ArrayList<Document> bookingsFromDB = coll.aggregate(aggregates).into(new ArrayList<Document>());
			
			for (final Document document : bookingsFromDB) {
				final User user = new User();
				user.setUserName(((Document) document.get("join_for_username",
								List.class).get(0))
								.getString(DBConstants.USER_NAME));
				user.setEmail(document.getString(DBConstants.EMAIL));
				user.setTeamName(((Document) document.get("join_for_username",
								List.class).get(0))
								.getString(DBConstants.TEAM_NAME));
				user.setBookedDateNTime(document.getDate(DBConstants.BOOKED_DATE_N_TIME));
				user.setEstimatedUsage(document.getInteger(DBConstants.ESTIMATED_USAGE));
				user.setBookingId(document.getString(DBConstants.BOOKING_ID));
				if (!bookings.containsKey(document.getString(DBConstants.BOX_NAME))) {
					Booking bkng = new Booking();
					bkng.setBoxName(document.getString(DBConstants.BOX_NAME));
					bkng.setBoxOwner(getBoxOwner(document
							.getString(DBConstants.BOX_NAME)));
					bkng.addUserToQueue(user);
					bookings.put(document.getString(DBConstants.BOX_NAME), bkng);
				} else {
					Booking bkng = bookings.get(document.getString(DBConstants.BOX_NAME));
					bkng.addUserToQueue(user);
				}
			}
			//sort users based on timestamps
			for (Booking bkng : bookings.values()) {
				Collections.sort(bkng.getUsersInQueue());
			}
		} catch (Exception e) {
			if (e instanceof com.mongodb.MongoTimeoutException) {
				throw new ApplicationException(MessagesEnum.MONGODB_IS_DOWN.getMessage(), e);
			}
			throw new ApplicationException(MessagesEnum.BOOKINGS_RETRIVAL_FAILED.getMessage(), e);
		}
		return bookings;
	}
	
	public boolean closeBooking(final String bookingId) {
		try {
			final MongoDatabase mdb = MongoDBConnManager.getInstance().getConnection();
			final MongoCollection<Document> coll = mdb.getCollection(DBConstants.COLL_BOOKING);
			final Document booking = new Document();
			booking.put(DBConstants.BOOKING_ID, bookingId);
			coll.deleteOne(booking);
		} catch (Exception e) {
			if (e instanceof com.mongodb.MongoTimeoutException) {
				throw new ApplicationException(MessagesEnum.MONGODB_IS_DOWN.getMessage(), e);
			}
			throw new ApplicationException(MessagesEnum.CLOSE_BOOKING_FAILED.getMessage(bookingId), e);
		}
		return true;
	}
	
	public List<User> getAllBookings(final String boxName) {
		/**
		 * Creating 'nextUserEmailInQueue' as string buffer because 
		 * value cannot be reassigned as it is declared as final
		 */
		final List<User> users = new ArrayList<User>();
		try {
			final MongoDatabase mdb = MongoDBConnManager.getInstance().getConnection();
			final MongoCollection<Document> coll = mdb.getCollection(DBConstants.COLL_BOOKING);
			final Document findCr = new Document();
			findCr.put(DBConstants.BOX_NAME, boxName);
			final Document sortCr = new Document();
			sortCr.put(DBConstants.BOOKED_DATE_N_TIME, 1);
			final ArrayList<Document> lstBkngs = coll.find(findCr).sort(sortCr).into(new ArrayList<Document>());
			
			for (final Document document : lstBkngs) {
				final User user = new User();
				user.setEmail(document.getString(DBConstants.EMAIL));
				user.setBookedDateNTime(document.getDate(DBConstants.BOOKED_DATE_N_TIME));
				user.setBookingId(document.getString(DBConstants.BOOKING_ID));
				users.add(user);
			}
		} catch (Exception e) {
			if (e instanceof com.mongodb.MongoTimeoutException) {
				throw new ApplicationException(MessagesEnum.MONGODB_IS_DOWN.getMessage(), e);
			}
			throw new ApplicationException(MessagesEnum.BOOKINGS_RETRIVAL_FAILED.getMessage(), e);
		}
		return users;
	}
	
	public User getBooking(final String bookingId) {
		/**
		 * Creating 'nextUserEmailInQueue' as string buffer because 
		 * value cannot be reassigned as it is declared as final
		 */
		final User user = new User();
		try {
			final MongoDatabase mdb = MongoDBConnManager.getInstance().getConnection();
			final MongoCollection<Document> coll = mdb.getCollection(DBConstants.COLL_BOOKING);
			final Document findCr = new Document();
			findCr.put(DBConstants.BOOKING_ID, bookingId);
			final ArrayList<Document> lstBkngs = coll.find(findCr).into(new ArrayList<Document>());
			
			for (final Document document : lstBkngs) {
				user.setEmail(document.getString(DBConstants.EMAIL));
				user.setBookingId(document.getString(DBConstants.BOOKING_ID));
			}
		} catch (Exception e) {
			if (e instanceof com.mongodb.MongoTimeoutException) {
				throw new ApplicationException(MessagesEnum.MONGODB_IS_DOWN.getMessage(), e);
			}
			throw new ApplicationException(MessagesEnum.BOOKINGS_RETRIVAL_FAILED.getMessage(), e);
		}
		return user;
	}
	
	public void updateBooking(String bookingId) {
		try {
			final MongoDatabase mdb = MongoDBConnManager.getInstance().getConnection();
			final MongoCollection<Document> coll = mdb.getCollection(DBConstants.COLL_BOOKING);
			//update user booking time
			final Document filterQuery = new Document();
			filterQuery.put(DBConstants.BOOKING_ID, bookingId);
			final Document updateQuery = new Document();
			final Document updateSet = new Document();	
			updateSet.put(DBConstants.DATE_N_TIME, new Date());
			updateQuery.put("$set", updateSet);
			coll.updateOne(filterQuery, updateQuery);
		} catch (Exception e) {
			if (e instanceof com.mongodb.MongoTimeoutException) {
				throw new ApplicationException(MessagesEnum.MONGODB_IS_DOWN.getMessage(), e);
			}
			throw new ApplicationException(MessagesEnum.UPDATE_USER_FAILED.getMessage(), e);
		}
	}
	
	@SuppressWarnings("deprecation")
	public boolean passBooking(User currentUserBooking, User nextUserInQueue) {
		try {
			final MongoDatabase mdb = MongoDBConnManager.getInstance().getConnection();
			final MongoCollection<Document> coll = mdb.getCollection(DBConstants.COLL_BOOKING);
			final Document findCr = new Document();
			findCr.put(DBConstants.BOOKING_ID, nextUserInQueue.getBookingId());
			final ArrayList<Document> lstBkngs = coll.find(findCr).into(new ArrayList<Document>());
			
			for (final Document document : lstBkngs) {
				java.util.Date nextUserBookingTime = document.getDate(DBConstants.BOOKED_DATE_N_TIME);
				nextUserBookingTime.setSeconds(nextUserBookingTime.getSeconds() + 1);
				currentUserBooking.setBookedDateNTime(nextUserBookingTime);
			}
			
			//update current user booking time
			final Document filterQuery = new Document();
			filterQuery.put(DBConstants.BOOKING_ID, currentUserBooking.getBookingId());
			final Document updateQuery = new Document();
			final Document updateSet = new Document();	
			updateSet.put(DBConstants.BOOKED_DATE_N_TIME, currentUserBooking.getBookedDateNTime());
			updateQuery.put("$set", updateSet);
			coll.updateOne(filterQuery, updateQuery);
		} catch (Exception e) {
			if (e instanceof com.mongodb.MongoTimeoutException) {
				throw new ApplicationException(MessagesEnum.MONGODB_IS_DOWN.getMessage(), e);
			}
			throw new ApplicationException(MessagesEnum.PASSING_BOOKING_FAILED.getMessage(), e);
		}
		return true;
	}
	
	public void clearAllBookings() {
		try {
			final MongoDatabase mdb = MongoDBConnManager.getInstance().getConnection();
			final MongoCollection<Document> coll = mdb.getCollection(DBConstants.COLL_BOOKING);
			final Document emptyDoc = new Document();
			coll.deleteMany(emptyDoc);
		} catch (Exception e) {
			if (e instanceof com.mongodb.MongoTimeoutException) {
				throw new ApplicationException(MessagesEnum.MONGODB_IS_DOWN.getMessage(), e);
			}
			throw new ApplicationException(MessagesEnum.CLEAR_ALL_BOOKINGS_FAILED.getMessage(), e);
		}
	}
	
	public boolean hasWhatsNewsBeenRead(String hostname) {
		try {
			final MongoDatabase mdb = MongoDBConnManager.getInstance().getConnection();
			final MongoCollection<Document> coll = mdb.getCollection(DBConstants.COLL_WHATSNEW);
			final Document bson = new Document();
			bson.put(DBConstants.HOST_NAME, hostname);
			final List<Document> documents = coll.find(bson).into(new ArrayList<Document>());
			if (documents == null || (documents != null && documents.size() == 0)) {
				return false;
			} else {
				//'0' index is accessed because hostname is uniquely maintained at DB level
				return documents.get(0).getString(DBConstants.READ) != null;
			}
		} catch (Exception e) {
			if (e instanceof com.mongodb.MongoTimeoutException) {
				throw new ApplicationException(MessagesEnum.MONGODB_IS_DOWN.getMessage(), e);
			}
			throw new ApplicationException(e);
		}
	}
	
	public void whatsNewsIsRead(String hostname) {
		try {
			final MongoDatabase mdb = MongoDBConnManager.getInstance().getConnection();
			final MongoCollection<Document> coll = mdb.getCollection(DBConstants.COLL_WHATSNEW);
			final Document document = new Document();
			document.put(DBConstants.HOST_NAME, hostname);
			document.put(DBConstants.READ, "Y");
			coll.insertOne(document);
		} catch (MongoWriteException e) {
			if (e.getCode() == 11000) {
				throw new ApplicationException(e);
			}
		} catch (Exception e) {
			if (e instanceof com.mongodb.MongoTimeoutException) {
				throw new ApplicationException(MessagesEnum.MONGODB_IS_DOWN.getMessage(), e);
			}
			throw new ApplicationException(e);
		}
	}
}
