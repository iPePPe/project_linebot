package com.zygen.hcp.jpa;

import static javax.persistence.TemporalType.TIMESTAMP;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;

import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.TenantDiscriminatorColumn;

@Entity
@Table(name = "MessageEvent")
@Multitenant
@TenantDiscriminatorColumn(name = "tenant_id", contextProperty = "me-tenant.id", length = 36)
@NamedQuery(name = "AllMessageEvent", query = "select p from MessageEvent p")
public class MessageEvent  {
//public class MessageEvent implements Serializable {
//	private static final long serialVersionUID = 1L;

	public MessageEvent() {
	}

	@Id
	@GeneratedValue
	private long id;
	private String type;
	private String replyToken;
	@Temporal(TIMESTAMP)
	private java.util.Date timestamp;
	private String userId;
	private String text;
	private String channel;
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name="userId", nullable=false, insertable=false, updatable=false)
	private UserProfile userProfile;

	public UserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String param) {
		this.type = param;
	}

	public String getReplyToken() {
		return replyToken;
	}

	public void setReplyToken(String param) {
		this.replyToken = param;
	}

	public java.util.Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(java.util.Date param) {
		this.timestamp = param;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String param) {
		this.userId = param;
	}

	public String getText() {
		return text;
	}

	public void setText(String param) {
		this.text = param;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String param) {
		this.channel = param;
	}

}