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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.zygen.linebot.model.message.imagemap.ImagemapAction;
import com.zygen.linebot.model.message.imagemap.ImagemapBaseSize;

import lombok.Value;

@Value
@JsonTypeName("imagemap")
public class ImagemapMessage implements Message {
	private final @JsonProperty String baseUrl;
	private final @JsonProperty String altText;
	private final @JsonProperty ImagemapBaseSize baseSize;
	private final @JsonProperty List<ImagemapAction> actions;

	public ImagemapMessage(String baseUrl,String altText,ImagemapBaseSize baseSize,
						   List<ImagemapAction> actions)
	{
		this.baseUrl = baseUrl;
		this.altText = altText;
		this.baseSize = baseSize;
		this.actions = actions;
	}
}
