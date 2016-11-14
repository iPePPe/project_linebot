package com.zygen.hcp.jpa;

import static javax.persistence.TemporalType.DATE;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;

import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.TenantDiscriminatorColumn;

import net.sf.sprockets.google.Place;

@Entity
@Table(name = "NicePlace")
@Multitenant
@TenantDiscriminatorColumn(name = "tenant_id", contextProperty = "me-tenant.id", length = 36)
@NamedQuery(name = "AllNicePlace", query = "select p from NicePlace p")
public class NicePlace implements Serializable {

	private static final long serialVersionUID = 1L;

	public NicePlace() {
	}

	public NicePlace(Place place) {

		this.latitude = place.getLatitude();
		this.longitude = place.getLongitude();
		this.vicinity = place.getVicinity();
		this.rating = place.getRating();
		this.placeId = place.getPlaceId().getId();
		this.name = place.getName();
		this.types = place.getTypes();
		this.address = place.getFormattedAddress();
		this.phoneNumber = place.getFormattedPhoneNumber();
		this.international_phone_number = place.getIntlPhoneNumber();
		this.website = place.getWebsite();

	}

	@Id
	private String placeId;
	private String name;
	@Column(name = "rating", precision = 1, scale = 2)
	private float rating;
	private String vicinity;
	private String website;
	private String international_phone_number;
	@ElementCollection
	private Collection<String> types;
	@Column(name = "LATITUDE", precision = 3, scale = 10)
	private double latitude;
	@Column(name = "LONGITUDE", precision = 3, scale = 10)
	private double longitude;
	private String secretCode;
	private String userId;
	private String address;
	private String phoneNumber;
	private int counter;
	@Temporal(DATE)
	private Date createDate;

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getPlaceId() {
		return placeId;
	}

	public void setPlaceId(String param) {
		this.placeId = param;
	}

	public String getName() {
		return name;
	}

	public void setName(String param) {
		this.name = param;
	}

	public float getRating() {
		return rating;
	}

	public void setRating(float param) {
		this.rating = param;
	}

	public String getVicinity() {
		return vicinity;
	}

	public void setVicinity(String param) {
		this.vicinity = param;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String param) {
		this.website = param;
	}

	public String getInternational_phone_number() {
		return international_phone_number;
	}

	public void setInternational_phone_number(String param) {
		this.international_phone_number = param;
	}

	public Collection<String> getTypes() {
		return types;
	}

	public void setTypes(Collection<String> param) {
		this.types = param;
	}

	public String getSecretCode() {
		return secretCode;
	}

	public void setSecretCode(String param) {
		this.secretCode = param;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String param) {
		this.userId = param;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String param) {
		this.address = param;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String param) {
		this.phoneNumber = param;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int param) {
		this.counter = param;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date param) {
		this.createDate = param;
	}

}