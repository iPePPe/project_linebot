package com.zygen.linebot.model.profile;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

/**
 * Response object for 'Get Profile' API.
 */
@Value
public class UserProfileResponse {
    public String getDisplayName() {
		return displayName;
	}

	public String getUserId() {
		return userId;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	/**
     * Display name
     */
    private final String displayName;

    /**
     * User ID
     */
    private final String userId;

    /**
     * Image URL
     */
    private final String pictureUrl;

    /**
     * Status message
     */
    private final String statusMessage;

    public UserProfileResponse(
            @JsonProperty("displayName") String displayName,
            @JsonProperty("userId") String userId,
            @JsonProperty("pictureUrl") String pictureUrl,
            @JsonProperty("statusMessage") String statusMessage) {
        this.displayName = displayName;
        this.userId = userId;
        this.pictureUrl = pictureUrl;
        this.statusMessage = statusMessage;
    }
}