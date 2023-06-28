package br.com.kafka.alura;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class KafkaService<T> implements Closeable {

    private final KafkaConsumer<String, T> consumer;
    private final ConsumerFunction parse;

    public KafkaService(String group, String topic, ConsumerFunction parse, Class<T> type, Map<String, String> properties) {
        this(parse, group, type, properties);
        consumer.subscribe(Collections.singletonList(topic));
    }

    public KafkaService(String group, Pattern topic, ConsumerFunction parse, Class<T> type, Map<String, String> properties) {
        this(parse, group, type, properties);
        consumer.subscribe(topic);
    }

    private KafkaService(ConsumerFunction parse, String group, Class<T> type, Map<String, String> properties) {
        this.consumer = new KafkaConsumer<>(getProperties(group, type, properties));
        this.parse = parse;
    }

    private Properties getProperties(String group, Class<T> type, Map<String, String> overrideProperties) {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:29092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, group);
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1"); //Para consumir 1 registro em cada poll
        properties.setProperty(GsonDeserializer.TYPE_CONFIG, type.getName());
        properties.putAll(overrideProperties);
        return properties;
    }

    public void run() {
        while (true) {
            var records = consumer.poll(Duration.ofMillis(100));
            if (!records.isEmpty()) {
                System.out.println("---------------------------");
                System.out.println("Encontrei " + records.count() + " registros.");
                for (var record : records) {
                    try {
                        parse.consume(record);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void close() {
        consumer.close();
    }
}
