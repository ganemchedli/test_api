package com.airquality ;

import okhttp3.*;

import java.io.IOException;


public class ApiTestMain {

    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.openaq.org/v2/averages?temporal=hour&locations_id=70084&spatial=location&limit=100&page=1")
                .get()
                .addHeader("accept", "application/json")
                .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // Handle failure
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // Handle successful response
                    if (response.isSuccessful()) {
                        // Use the response body as needed
                        String responseBody = response.body().string();
                        System.out.println(responseBody);
                    } else {
                        // Handle unsuccessful response
                        System.out.println("Unsuccessful response: " + response.code() + " " + response.message());
                    }
                }
            });
    }
}