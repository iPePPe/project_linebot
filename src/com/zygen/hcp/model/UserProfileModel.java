package com.zygen.hcp.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zygen.hcp.jpa.MessageEvent;
import com.zygen.hcp.jpa.UserProfile;
import com.zygen.linebot.client.LineMessagingServiceBuilder;
import com.zygen.linebot.model.profile.UserProfileResponse;

import retrofit2.Response;

public class UserProfileModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserProfileModel.class);

	public UserProfile checkCreateUserProfile(EntityManager em, String tanantId,String channelAccessToken,MessageEvent me) {
		UserProfile user = em.find(UserProfile.class, me.getUserId());
		if(user != null){
			
			updateUserProfile(user,channelAccessToken,me);
		}else{
			user = getUserProfile(channelAccessToken,me.getUserId());
		}
		return user;
	}
	public UserProfile checkCreateUserProfileRaduis(EntityManager em,  String tanantId,String channelAccessToken,int r, MessageEvent jpe) {

		//Query query = em.createQuery("SELECT p FROM UserProfile p WHERE p.userId ='" + userId+ "'");
		UserProfile user = em.find(UserProfile.class, jpe.getUserId());
		if(user != null){
			
			user = updateUserProfile(user,channelAccessToken,jpe);
			user.setRadius(r);
		}else{
			user = getUserProfile(channelAccessToken,jpe.getUserId());
		}
		return user;
	}
	public UserProfile checkUserProfileLocation(EntityManager em,  String tanantId,String channelAccessToken,MessageEvent me) {
		
		UserProfile user = em.find(UserProfile.class, me.getUserId());
		if(user != null){
			
			user = updateUserLocation(user,channelAccessToken,me);

		}else{
			user = getUserProfile(channelAccessToken,me.getUserId());
			user = updateUserLocation(user,channelAccessToken,me);

		}

		return user;
	}
	public UserProfile getUserProfile(String channelAccessToken,String userId) {
		Response<UserProfileResponse> response;
		UserProfile user = new UserProfile();
		try {
			
			response = LineMessagingServiceBuilder.create(channelAccessToken).build().getProfile(userId)
					.execute();

			if (response.isSuccessful()) {
				UserProfileResponse profile = response.body();
				user.setDisplayName(profile.getDisplayName());
				user.setPictureUrl(profile.getPictureUrl());
				user.setStatusMessage(profile.getStatusMessage());
				user.setStatus("New");
				user.setCreateDate(new Date());
				user.setUserId(userId);

			} else {
				//LOGGER.error(response.message());
				//System.out.println(response.code() + " " + response.message());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;
	}
	public UserProfile getUserProfile(String channelAccessToken,MessageEvent me) {

		return getUserProfile(channelAccessToken,me.getUserId());
	}
	public UserProfile updateUserProfile(UserProfile user,String channelAccessToken,MessageEvent me) {
		Response<UserProfileResponse> response;
		try {
			response = LineMessagingServiceBuilder.create(channelAccessToken).build().getProfile(me.getUserId())
					.execute();

			if (response.isSuccessful()) {
				UserProfileResponse profile = response.body();
				user.setDisplayName(profile.getDisplayName());
				user.setPictureUrl(profile.getPictureUrl());
				user.setStatusMessage(profile.getStatusMessage());
				user.setLastActionDate(new Date());
				user.setUserId(me.getUserId());
				ArrayList<MessageEvent> mel = new ArrayList<MessageEvent>();
				mel.add(me);
				user.setMessageEvent(mel);
			} else {
				LOGGER.error(response.message());
				//System.out.println(response.code() + " " + response.message());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;
	}
	public UserProfile updateUserProfileRadius(UserProfile user,String channelAccessToken,int r, MessageEvent jpe) {
		
		user = updateUserProfile(user,channelAccessToken,jpe);
		user.setRadius(r);

		return user;
	}
	public UserProfile updateUserLocation(UserProfile user,String channelAccessToken,MessageEvent me) {
		
		user = updateUserProfile(user,channelAccessToken,me);
		user.setUserId(me.getUserId());
		user.setLatitude(me.getLatitude());
		user.setLongitude(me.getLongitude());
		user.setLocationDate(new Date());
		user.setLocationTitle(me.getAddress());
		
		return user;
	}
}
