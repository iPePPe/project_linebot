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

package com.zygen.linebot.model;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.zygen.linebot.model.message.Message;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class PushMessage {
    private final @JsonProperty String to;
    private final @JsonProperty List<Message> messages;

    public PushMessage(String to, Message message) {
        this.to = to;
       // this.messages = Collections.singletonList(message);
        this.messages = Arrays.asList(message);
    }
	public PushMessage(String to,List<Message> message) {
		this.to = to;
		this.messages = message;

	}
	public String toJSON(boolean prettyPrint) throws JsonGenerationException, JsonMappingException, IOException {
		String json = null;
		ObjectMapper mapper = new ObjectMapper(); // Setup Jackson
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		//mapper.configure(MapperFeature.INDENT_OUTPUT, true);
		//mapper.configure(Feature.SORT_PROPERTIES_ALPHABETICALLY, true);

		ObjectWriter writer = mapper.writer();
		if (prettyPrint)
			writer = writer.withDefaultPrettyPrinter();

		json = writer.writeValueAsString(this);

		return json;
	}
}
