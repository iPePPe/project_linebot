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

package com.zygen.linebot.model.event;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import com.zygen.linebot.model.event.message.MessageContent;
import com.zygen.linebot.model.event.source.Source;

import lombok.Value;

@Value
@JsonTypeName("message")
public class MessageEvent implements Event {
    private final String replyToken;
    private final Source source;
    private final MessageContent message;
    private final Instant timestamp;

    @JsonCreator
    public MessageEvent(
            @JsonProperty("replyToken") String replyToken,
            @JsonProperty("source") Source source,
            @JsonProperty("message") MessageContent message,
            @JsonProperty("timestamp") Instant timestamp) {
        this.replyToken = replyToken;
        this.source = source;
        this.message = message;
        this.timestamp = timestamp;
    }
    @Override
    public Instant getTimestamp(){
    	return this.timestamp;
    }
    @Override
    public Source getSource(){
    	return this.source;
    }
    public MessageContent getMessage(){
    	return this.message;
    }
    public String getReplyToken(){
    	return this.replyToken;
    }
}
