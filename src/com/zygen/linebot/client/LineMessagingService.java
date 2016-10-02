package com.zygen.linebot.client;




import com.zygen.linebot.model.ReplyMessage;
import com.zygen.linebot.model.response.BotApiResponse;

//import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
//import retrofit2.http.GET;
import retrofit2.http.POST;
//import retrofit2.http.Path;
//import retrofit2.http.Streaming;

public interface LineMessagingService {
    @POST("/v2/bot/message/reply")
    Call<BotApiResponse> replyMessage(@Body ReplyMessage replyMessage);


}

