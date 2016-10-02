package com.zygen.linebot.model.message;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zygen.linebot.model.message.Message;
import lombok.Value;

@Value
public class ResponeMessage {
	private final List<Message> messages;

	public ResponeMessage(@JsonProperty("messages") List<Message> messages){
		this.messages = messages;
	}
    public List<Message> getMessages(){
    	return this.messages;
    }

}
