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

package com.zygen.linebot.model.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Value;

@Value
@JsonTypeName("location")
public class LocationMessage implements Message {
    private final @JsonProperty String title;
    private final @JsonProperty String address;
    private final @JsonProperty double latitude;
    private final @JsonProperty double longitude;
    public LocationMessage(String title,String address,double latitude,double longitude){
    	this.title = title;
    	this.address = address;
    	this.latitude = latitude;
    	this.longitude = longitude;
    }
}
