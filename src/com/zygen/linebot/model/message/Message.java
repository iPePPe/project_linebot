package com.zygen.linebot.model.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = As.PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
                      @JsonSubTypes.Type(TextMessage.class),
                      @JsonSubTypes.Type(ImageMessage.class),
                      @JsonSubTypes.Type(StickerMessage.class),
                      @JsonSubTypes.Type(LocationMessage.class),
                      @JsonSubTypes.Type(AudioMessage.class),
                      @JsonSubTypes.Type(ImagemapMessage.class),
                      @JsonSubTypes.Type(TemplateMessage.class)
              })
public interface Message {
}