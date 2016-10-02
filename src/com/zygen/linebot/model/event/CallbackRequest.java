package com.zygen.linebot.model.event;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zygen.linebot.model.event.Event;

import lombok.Value;

@Value
public class CallbackRequest {
    private final List<Event> events;

    public CallbackRequest(@JsonProperty("events") List<Event> events) {
        this.events = events;
    }
    public List<Event> getEvents(){
    	return this.events;
    }
}

