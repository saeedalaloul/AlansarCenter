package com.alansar.center.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "content_Type:application/json",
                    "Authorization:key=AAAAeTE1Q_w:APA91bEkh8DLkabC8ixb_rnhME27Ybm9JbbG5m8V-ppE1d-kIwnyAJUKsv4fNop6bq1S3uvatvtXsWoiCSNWFL8UahUhwdxMZQ0Lbt9HrPwBgb9DX2NTwZK9kffTDgtBXd0q8_ORh_Di"
            }
    )

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
