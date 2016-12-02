package com.zygen.ai.api;

/***********************************************************************************************************************
 *
 * API.AI Java SDK - client-side libraries for API.AI
 * =================================================
 *
 * Copyright (C) 2016 by Speaktoit, Inc. (https://www.speaktoit.com)
 * https://www.api.ai
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************/

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import com.zygen.ai.api.model.ResponseMessage;
import com.zygen.ai.api.model.ResponseMessage.MessageType;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class GsonFactory {
	
	private static final Gson DEFAULT_GSON = new GsonBuilder().create();

    private static final Gson PROTOCOL_GSON = new GsonBuilder()
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).toPattern())
            .registerTypeAdapter(ResponseMessage.class, new ResponseItemDeserializer())
            .registerTypeAdapter(ResponseMessage.ResponseSpeech.class, new ResponseSpeechDeserializer())
            .create();
    
    private static final GsonFactory DEFAULT_FACTORY = new GsonFactory();

    public Gson getGson(){
        return PROTOCOL_GSON;
    }
    
    public static GsonFactory getDefaultFactory() {
    	return DEFAULT_FACTORY;
    }
    
    private static class ResponseItemDeserializer implements JsonDeserializer<ResponseMessage> {
    	public ResponseMessage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
  		      throws JsonParseException {
	  		
	  		int typeCode = json.getAsJsonObject().get("type").getAsInt();
	  		
	  		for (MessageType type : MessageType.values()) {
	  			if (type.getCode() == typeCode) {
	  				return context.deserialize(json, type.getType());
	  			}
	  		}
	  		
	  		throw new JsonParseException(String.format("Unexpected message type value: %d", typeCode));
	  	}
    }
    
    private static class ResponseSpeechDeserializer implements JsonDeserializer<ResponseMessage> {
    	public ResponseMessage.ResponseSpeech deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
    		      throws JsonParseException {
    		
    		if (json.isJsonObject() && ((JsonObject)json).get("speech").isJsonPrimitive()) {
    			JsonArray array = new JsonArray();
    			array.add(((JsonObject)json).get("speech"));
    			((JsonObject)json).add("speech", array);
    		}
    		
    		return DEFAULT_GSON.fromJson(json, typeOfT);
    	}    	
    }
}
