package com.airquality;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaConsumerMain {

    public static void main(String[] args) {
        // Kafka consumer configuration
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "my-consumer-group");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        // Create Kafka consumer
        Consumer<String, String> consumer = new KafkaConsumer<>(properties);

        // Subscribe to the Kafka topic
        consumer.subscribe(Collections.singletonList("topic1"));

        // Output file path
        String filePath = "received_messages.txt";

        // Initialize the writer outside the try-with-resources to allow access in the shutdown hook
        BufferedWriter[] writer = {null};  // Use an array to hold a mutable reference

        try {
            writer[0] = new BufferedWriter(new FileWriter(filePath, true));

            // Add a shutdown hook to handle program termination
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                // Close the writer and flush the contents on program termination
                try {
                    if (writer[0] != null) {
                        writer[0].close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));

            // Poll for messages
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                records.forEach(record -> {
                    String message = record.value();
                    System.out.println("Received message: " + message);

                    // Write the message to the file
                    try {
                        writer[0].write(message);
                        writer[0].newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                // Flush the contents to the file after each poll
                writer[0].flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close the writer in case of an exception
            try {
                if (writer[0] != null) {
                    writer[0].close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
