package com.mbeans;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.constants.DBConstants;
import com.dao.DataAccess;
import com.exception.MessagesEnum;

@ViewScoped
@ManagedBean(name="registrationmb")
public class UserRegistration extends BaseMBean {
	private String username;
	private String email;
	private String teamname;
	private boolean status;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTeamname() {
		return teamname;
	}

	public void setTeamname(String teamname) {
		this.teamname = teamname;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String registerUser() {
		if (!super.validateEmail(email)) {
			return null;
		}
		
		final Map<String, String> userInfo = new LinkedHashMap<String, String>();
		userInfo.put(DBConstants.USER_NAME, username);
		userInfo.put(DBConstants.EMAIL, email);
		userInfo.put(DBConstants.TEAM_NAME, teamname);
		
		status = DataAccess.getInstance().addUser(userInfo);
			
		if (status) {
			/*FacesContext facesContext = FacesContext.getCurrentInstance();
			Flash flash = facesContext.getExternalContext().getFlash();
			flash.setKeepMessages(true);
			flash.setRedirect(true);
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"", ""));*/
			super.addInfoMessage(MessagesEnum.ADD_USER_SUCCESS.getMessage(username) + " " + MessagesEnum.NAV_BOX_BOOKING.getMessage());
		}
		return null;
	}
}
