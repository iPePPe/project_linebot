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

import com.zygen.linebot.model.event.postback.PostbackContent;
import com.zygen.linebot.model.event.source.Source;

import lombok.Value;

@Value
@JsonTypeName("postback")
public class PostbackEvent implements Event {
	private String replyToken;
    private Source source;
    private PostbackContent postbackContent;
    private Instant timestamp;

    @JsonCreator
    public PostbackEvent(
            @JsonProperty("replyToken") String replyToken,
            @JsonProperty("source") Source source,
            @JsonProperty("postback") PostbackContent postbackContent,
            @JsonProperty("timestamp") Instant timestamp) {
        this.replyToken = replyToken;
        this.source = source;
        this.postbackContent = postbackContent;
        this.timestamp = timestamp;
    }
	
    public String getReplyToken() {
		return replyToken;
	}

	public void setReplyToken(String replyToken) {
		this.replyToken = replyToken;
	}

	public PostbackContent getPostbackContent() {
		return postbackContent;
	}

	public void setPostbackContent(PostbackContent postbackContent) {
		this.postbackContent = postbackContent;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public Source getSource() {
		// TODO Auto-generated method stub
		return this.source;
	}

	@Override
	public Instant getTimestamp() {
		// TODO Auto-generated method stub
		return this.timestamp;
	}
}
