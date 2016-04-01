package com.mbeans;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.constants.ApplicationConstants;
import com.constants.DBConstants;
import com.dao.DataAccess;
import com.exception.ApplicationException;
import com.exception.MessagesEnum;
import com.services.EmailServiceImpl;
import com.util.PropertiesUtil;
import com.vo.Booking;
import com.vo.User;

@RequestScoped
@ManagedBean(name="boxbookingmb")
public class BoxBooking extends BaseMBean {
	
	private static final long serialVersionUID = 3071152249212006233L;
	
	private String email;
	private String boxselected;
	private int estimatedUsage;
	private List<SelectItem> boxes = new ArrayList<SelectItem>();
	private List<Booking> bookings;
	private Booking selectedRow;
	private boolean whatsnewdlgvisible = false;

	public boolean isWhatsnewdlgvisible() {
		return whatsnewdlgvisible;
	}

	public void setWhatsnewdlgvisible(boolean whatsnewdlgvisible) {
		this.whatsnewdlgvisible = whatsnewdlgvisible;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getBoxselected() {
		/*Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
		if (selectedRow == null && flash.get(ApplicationConstants.CURRENT_BOX_BOOKING) != null) {
			selectedRow = (Booking) flash.get(ApplicationConstants.CURRENT_BOX_BOOKING);
		}*/
		FacesContext context = FacesContext.getCurrentInstance();
		Object currBooking = context.getExternalContext().getRequestMap()
				.get(ApplicationConstants.CURRENT_BOX_BOOKING);
		if (selectedRow == null && currBooking != null) {
			selectedRow = (Booking) currBooking;
		}
		if (selectedRow != null) {
			setBoxselected(selectedRow.getBoxName());
		}
		return boxselected;
	}

	public void setBoxselected(String boxselected) {
		this.boxselected = boxselected;
	}

	public int getEstimatedUsage() {
		return estimatedUsage;
	}

	public void setEstimatedUsage(int estimatedUsage) {
		this.estimatedUsage = estimatedUsage;
	}

	public List<SelectItem> getBoxes() {
		if (boxes.size() == 0) {
			final Map<String, List<String>> boxesFromDB = DataAccess.getInstance()
					.getBoxes();
			for (final String teamname : boxesFromDB.keySet()) {
				SelectItemGroup group = new SelectItemGroup(teamname);
				SelectItem[] items = new SelectItem[boxesFromDB.get(teamname)
						.size()];
				int i = 0;
				for (final String boxname : boxesFromDB.get(teamname)) {
					items[i] = new SelectItem(boxname, boxname);
					i++;
				}
				group.setSelectItems(items);
				boxes.add(group);
			}
		}
		return boxes;
	}

	public void setBoxes(List<SelectItem> boxes) {
		this.boxes = boxes;
	}
	
	public List<Booking> getBookings() {
		//if (bookings == null) {
			bookings = new ArrayList<Booking>(DataAccess.getInstance().getAllBookings().values());
		//}
		return bookings;
	}

	public void setBookings(List<Booking> bookings) {
		this.bookings = bookings;
	}

	public Booking getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(Booking selectedRow) {
		this.selectedRow = selectedRow;
	}
	
	public String book() {
		if (!super.validateEmail(email)) {
			return null;
		}
		
		final String userName = DataAccess.getInstance().getUserNameWithEmail(email);
		
		if (StringUtils.isBlank(userName)) {
			throw new ApplicationException(MessagesEnum.USER_DOES_NOT_EXISTS.getMessage(email));
		}
		
		final Map<String, Object> bookingInfo = new LinkedHashMap<String, Object>();
		bookingInfo.put(DBConstants.EMAIL, email);
		bookingInfo.put(DBConstants.BOX_NAME, boxselected);
		bookingInfo.put(DBConstants.BOOKED_DATE_N_TIME, new Date());
		bookingInfo.put(DBConstants.DATE_N_TIME, new Date());
		bookingInfo.put(DBConstants.ESTIMATED_USAGE, estimatedUsage);
		bookingInfo.put(DBConstants.BOOKING_ID, "" + System.currentTimeMillis() + "");
		
		final boolean status = DataAccess.getInstance().saveBooking(bookingInfo);
		
		if (status) {
			final Map<String, Booking> bookingsMap = DataAccess.getInstance().getAllBookings();
			
			final List<User> userQueue = bookingsMap.get(boxselected).getUsersInQueue();
			if (userQueue != null && userQueue.size() > 0) {
				for (User user : userQueue) {
					if (StringUtils.equalsIgnoreCase(user.getEmail(), email)) {
						user.setHighlight(true);
					}
				}
			}
			
			bookings = new ArrayList<Booking>(bookingsMap.values());
			
			/*Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
			flash.put(ApplicationConstants.CURRENT_BOX_BOOKING, bookingsMap.get(boxselected));
			
			flash.setKeepMessages(true);
			flash.setRedirect(true);*/
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getRequestMap().put(ApplicationConstants.CURRENT_BOX_BOOKING, bookingsMap.get(boxselected));
			
			super.addInfoMessage(MessagesEnum.BOOKING_SUCCESS.getMessage(
					bookingInfo.get(DBConstants.BOX_NAME)));
			
			//send booking email
			/*Properties props = System.getProperties();
			props.setProperty("mail.transport.protocol", "smtp");
			props.setProperty("mail.host", "10.118.23.1");
			props.setProperty("mail.smtp.port", "25");
			props.setProperty("mail.smtp.user", "donotreply@deloitte.com");*/
			
			final Properties props = PropertiesUtil.getInstance().getDmailProps();
			
			final ExternalContext ctxt = FacesContext.getCurrentInstance().getExternalContext();
			final String subject = MessagesEnum.BOOKING_EMAIL_SUBJECT_TEMPLATE.getMessage(boxselected);
			final String body = MessagesEnum.BOOKING_EMAIL_BODY_TEMPLATE
								.getMessage(
										boxselected,
										ctxt.getRequestServerName(),
										"" + ctxt.getRequestServerPort() + "",
										ctxt.getRequestContextPath().substring(1,
												ctxt.getRequestContextPath().length()),
										bookingInfo.get(DBConstants.BOX_NAME),
										bookingInfo.get(DBConstants.BOOKING_ID),
										ApplicationConstants.ADMIN_EMAIL);
			
			try {
				EmailServiceImpl.getInstance().sendMail(props, email, subject, body);
			} catch (Exception e) {
				throw new ApplicationException(MessagesEnum.EMAIL_SENDING_FAILED.getMessage(email));
			}
			
			//What's New Notification
			String whatsnewfeature = (String) PropertiesUtil.getInstance().getProps().get(ApplicationConstants.WHATS_NEW_FEATURE);
			if (StringUtils.endsWithIgnoreCase(whatsnewfeature, "on")) {
				String hostname = ((HttpServletRequest) context.getExternalContext()
						.getRequest()).getRemoteHost();
				if (!DataAccess.getInstance().hasWhatsNewsBeenRead(hostname)) {
					setWhatsnewdlgvisible(true);
				}
			}
		}
		
		return "usersinqueue";
	}
	
	public void whatsnewread() {
		String hostname = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest()).getRemoteHost();
		DataAccess.getInstance().whatsNewsIsRead(hostname);
	}
}
