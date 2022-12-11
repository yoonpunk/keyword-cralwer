package com.beige.keywordcrawler.service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class LineNotifyService {

    @Value("${line.notify.token:null}")
    private String token;

    public void sendNotifiy(String message) throws IOException {

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("	application/x-www-form-urlencoded");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("message", message)
                .build();
        Request request = new Request.Builder()
                .url("https://notify-api.line.me/api/notify")
                .method("POST", body)
                .addHeader("Content-Type", "	application/x-www-form-urlencoded")
                .addHeader("Authorization", "	Bearer " + token)
                .build();
        Response response = client.newCall(request).execute();
    }
}
