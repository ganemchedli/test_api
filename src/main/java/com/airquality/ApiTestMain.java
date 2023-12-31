package com.airquality;

import okhttp3.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.util.Properties;

public class ApiTestMain {

    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.openaq.org/v2/averages?temporal=hour&locations_id=70084&spatial=location&limit=100&page=1")
                .get()
                .addHeader("accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                System.out.println(responseBody);

                // Kafka producer configuration
Properties properties = new Properties();
properties.put("bootstrap.servers", "localhost:9092"); // Update this line
properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

                try (Producer<String, String> producer = new KafkaProducer<>(properties)) {
                    // Send the API response to Kafka topic
                  // Send the API response to Kafka topic
                  producer.send(new ProducerRecord<>("topic1", responseBody)); // Update this line

                }
            } else {
                System.out.println("Unsuccessful response: " + response.code() + " " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
