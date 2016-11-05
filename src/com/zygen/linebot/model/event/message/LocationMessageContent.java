/*
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.zygen.linebot.model.event.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Value;

@Value
@JsonTypeName("location")
public class LocationMessageContent implements MessageContent {


	private final String id;
    private final String type;
    private final String title;
    private final String address;
    private final double latitude;
    private final double longitude;

    @JsonCreator
    public LocationMessageContent(
            @JsonProperty("id") String id,
            @JsonProperty("type") String type,
            @JsonProperty("title") String title,
            @JsonProperty("address") String address,
            @JsonProperty("latitude") Double latitude,
            @JsonProperty("longitude") Double longitude) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}
    public String getType() {
		return type;
	}

	public String getTitle() {
		return title;
	}

	public String getAddress() {
		return address;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}
}