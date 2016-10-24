package com.zygen.linebot.client;




import com.zygen.linebot.model.PushMessage;
import com.zygen.linebot.model.ReplyMessage;
import com.zygen.linebot.model.profile.UserProfileResponse;
import com.zygen.linebot.model.response.BotApiResponse;

//import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
//import retrofit2.http.GET;
import retrofit2.http.POST;
//import retrofit2.http.Path;
//import retrofit2.http.Streaming;
import retrofit2.http.Path;

public interface LineMessagingService {
    @POST("/v2/bot/message/reply")
    Call<BotApiResponse> replyMessage(@Body ReplyMessage replyMessage);

    @POST("/v2/bot/message/push")
    Call<BotApiResponse> pushMessage(@Body PushMessage pushMessage);

    @GET("/v2/bot/profile/{userId}")
    Call<UserProfileResponse> getProfile(@Path("userId") String userId);



}

