package com.zygen.hcp.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zygen.hcp.jpa.MessageEvent;
import com.zygen.hcp.jpa.UserProfile;
import com.zygen.linebot.callback.CallbackHCPServlet;
import com.zygen.linebot.client.LineMessagingServiceBuilder;
import com.zygen.linebot.model.profile.UserProfileResponse;

import retrofit2.Response;

public class UserProfileModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(CallbackHCPServlet.class);

	public UserProfile checkCreateUserProfile(EntityManager em, String userId, String tanantId,String channelAccessToken,MessageEvent me) {

		Query query = em.createQuery("SELECT p FROM UserProfile p WHERE p.userId ='" + userId+ "'");
		UserProfile user = new UserProfile();
		try {
			user = (UserProfile) query.getSingleResult();
			updateUserProfile(user,channelAccessToken,userId,me);
			
		} catch (NoResultException e) {
			

			user = getUserProfile(channelAccessToken,userId,me);
			
			
		}
		return user;
	}
	public UserProfile checkCreateUserProfileRaduis(EntityManager em, String userId, String tanantId,String channelAccessToken,int r, MessageEvent jpe) {

		Query query = em.createQuery("SELECT p FROM UserProfile p WHERE p.userId ='" + userId+ "'");
		UserProfile user = new UserProfile();
		try {
			user = (UserProfile) query.getSingleResult();
			updateUserProfileRadius(user,channelAccessToken,userId,r,jpe);
			
		} catch (NoResultException e) {
			

			user = getUserProfile(channelAccessToken,userId);
			
			
		}
		return user;
	}
	public UserProfile checkUserProfileLocation(EntityManager em, String userId, String tanantId,String channelAccessToken,MessageEvent me) {
		
		Query query = em.createQuery("SELECT p FROM UserProfile p WHERE p.userId ='" + userId+ "'");
		UserProfile user = new UserProfile();
		try {
			user = (UserProfile) query.getSingleResult();
			updateUserLocation(user,channelAccessToken,userId,me);

		} catch (NoResultException e) {
			

			user = getUserProfile(channelAccessToken,userId,me);
			
			
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
				LOGGER.error(response.message());
				//System.out.println(response.code() + " " + response.message());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;
	}
	public UserProfile getUserProfile(String channelAccessToken,String userId,MessageEvent me) {
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
	public UserProfile updateUserProfile(UserProfile user,String channelAccessToken,String userId,MessageEvent me) {
		Response<UserProfileResponse> response;
		//UserProfile user = new UserProfile();
		try {
			response = LineMessagingServiceBuilder.create(channelAccessToken).build().getProfile(userId)
					.execute();

			if (response.isSuccessful()) {
				UserProfileResponse profile = response.body();
				user.setDisplayName(profile.getDisplayName());
				user.setPictureUrl(profile.getPictureUrl());
				user.setStatusMessage(profile.getStatusMessage());
				user.setLastActionDate(new Date());
				user.setUserId(userId);
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
	public UserProfile updateUserProfileRadius(UserProfile user,String channelAccessToken,String userId,int r, MessageEvent jpe) {
		Response<UserProfileResponse> response;
		//UserProfile user = new UserProfile();
		try {
			response = LineMessagingServiceBuilder.create(channelAccessToken).build().getProfile(userId)
					.execute();

			if (response.isSuccessful()) {
				UserProfileResponse profile = response.body();
				user.setDisplayName(profile.getDisplayName());
				user.setPictureUrl(profile.getPictureUrl());
				user.setStatusMessage(profile.getStatusMessage());
				user.setLastActionDate(new Date());
				user.setUserId(userId);
				user.setRadius(r);
				ArrayList<MessageEvent> pel = new ArrayList<MessageEvent>();
				pel.add(jpe);
				user.setMessageEvent(pel);
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
	public UserProfile updateUserLocation(UserProfile user,String channelAccessToken,String userId,MessageEvent me) {
		Response<UserProfileResponse> response;
		//UserProfile user = new UserProfile();
		try {
			response = LineMessagingServiceBuilder.create(channelAccessToken).build().getProfile(userId)
					.execute();

			if (response.isSuccessful()) {
				UserProfileResponse profile = response.body();
				user.setDisplayName(profile.getDisplayName());
				user.setPictureUrl(profile.getPictureUrl());
				user.setStatusMessage(profile.getStatusMessage());
				user.setLastActionDate(new Date());
				user.setUserId(userId);
				user.setLatitude(me.getLatitude());
				user.setLongitude(me.getLongitude());
				user.setLocationDate(new Date());
				user.setLocationTitle(me.getAddress());
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
}
