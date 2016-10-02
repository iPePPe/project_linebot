package com.zygen.linebot.model.event;


import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.zygen.linebot.model.event.source.Source;


@JsonSubTypes({
        @JsonSubTypes.Type(MessageEvent.class)
       // @JsonSubTypes.Type(UnfollowEvent.class),
       // @JsonSubTypes.Type(FollowEvent.class),
       // @JsonSubTypes.Type(JoinEvent.class),
       // @JsonSubTypes.Type(LeaveEvent.class),
       // @JsonSubTypes.Type(PostbackEvent.class),
       // @JsonSubTypes.Type(BeaconEvent.class)
})
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        defaultImpl = UnknownEvent.class,
        visible = true
)
public interface Event {
    Source getSource();

    Instant getTimestamp();
}