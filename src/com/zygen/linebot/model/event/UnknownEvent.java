package com.zygen.linebot.model.event;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.zygen.linebot.model.event.source.Source;
import lombok.Value;

/**
 * Fallback event type for {@link Event}.
 */
@Value
public class UnknownEvent implements Event {
    private final String type;
    private final Source source;
    private final Instant timestamp;

    public UnknownEvent(
            @JsonProperty("type") String type,
            @JsonProperty("source") Source source,
            @JsonProperty("timestamp") Instant timestamp
    ) {
        this.type = type;
        this.source = source;
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
}
