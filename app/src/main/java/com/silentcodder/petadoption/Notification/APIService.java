package com.silentcodder.petadoption.Notification;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAB_47A7g:APA91bFyL0KqhN5te4AelQMdv-PslwZ9HOR97ie4sWdP4PTDb7SyyBsaxxWvo5Vx-qNOpB3HXJv6Mj_6To7d06MimoE8YocJSzlLYzoBGsVk9YwQ3L0nyC5w_iQ_QGWiKGIyQHtMmbhG"
    })

    @POST("fcm/send")
    Call<ResponseBody> sendNotification(@Body NotificationSender body);

}
