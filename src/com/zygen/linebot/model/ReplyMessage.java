package com.zygen.linebot.model;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.zygen.linebot.model.message.Message;


import lombok.AllArgsConstructor;
import lombok.Value;





@Value
@AllArgsConstructor
public class ReplyMessage {
	private static final long serialVersionUID = 1L;
	private final @JsonProperty String replyToken;
	private final @JsonProperty List<Message> messages;

	public ReplyMessage(String replyToken, Message message) {

		this.replyToken = replyToken;
		this.messages = Arrays.asList(message);

	}
	public ReplyMessage(String replyToken, List<Message> message) {

		this.replyToken = replyToken;
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
