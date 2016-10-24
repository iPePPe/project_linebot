package com.zygen.hcp.jpa;


import javax.persistence.*;
import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.TenantDiscriminatorColumn;

import com.zygen.hcp.jpa.MessageEvent;
import java.util.Collection;
import java.util.Date;
import static javax.persistence.TemporalType.DATE;
import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
@Table(name = "UserProfile")
@Multitenant
@TenantDiscriminatorColumn(name = "tenant_id", contextProperty = "me-tenant.id", length = 36)
@NamedQuery(name = "AllUserProfile", query = "select p from UserProfile p")
public class UserProfile  {
//public class UserProfile implements Serializable {
//	private static final long serialVersionUID = 1L;

	public UserProfile() {
	}

	public UserProfile(String userId) {
		this.userId = userId;

	}

	@Id
	private String userId;
	private String pictureUrl;
	private String displayName;
	private String statusMessage;

	private String status;
	@OneToMany(targetEntity=MessageEvent.class,mappedBy = "userProfile", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name="userId", nullable=false, insertable=false, updatable=false)
	private Collection<MessageEvent> messageEvent;
	@Temporal(DATE)
	private Date createDate;
	@Temporal(TIMESTAMP)
	private Date lastActionDate;
	private String sex;
	@Temporal(DATE)
	private Date birthDay;
	private String langu;
	private String country;
	private String city;
	private String mobile;
	private String bloodGroup;
	private String email;
	

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String param) {
		this.pictureUrl = param;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String param) {
		this.displayName = param;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String param) {
		this.statusMessage = param;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String param) {
		this.userId = param;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String param) {
		this.status = param;
	}

	public Collection<MessageEvent> getMessageEvent() {
		return messageEvent;
	}

	public void setMessageEvent(Collection<MessageEvent> param) {
		this.messageEvent = param;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date param) {
		this.createDate = param;
	}

	public Date getLastActionDate() {
		return lastActionDate;
	}

	public void setLastActionDate(Date param) {
		this.lastActionDate = param;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String param) {
		this.sex = param;
	}

	public Date getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(Date param) {
		this.birthDay = param;
	}

	public String getLangu() {
		return langu;
	}

	public void setLangu(String param) {
		this.langu = param;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String param) {
		this.country = param;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String param) {
		this.city = param;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String param) {
		this.mobile = param;
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String param) {
		this.bloodGroup = param;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String param) {
		this.email = param;
	}

}