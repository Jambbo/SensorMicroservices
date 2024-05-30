package com.example.datageneratormicroservice.config;

import com.jcabi.xml.XML;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String servers; // kafka servers where we will send an info

    private final XML settings;

    //Describing the kafka topics

    @Bean//topic that will save and store the data from sensors by temperature
    public NewTopic temperatureTopic() {
        return TopicBuilder.name("data-temperature")
                .partitions(5) //here should be an odd number of partitions to make kafka normally working
                .replicas(1)
                .config(
                        TopicConfig.RETENTION_MS_CONFIG,
                        String.valueOf(Duration.ofDays(7).toMillis()) // how long the data will be stored in partitions
                )
                .build();
    }

    @Bean//topic that will save and store the data from sensors by voltage
    public NewTopic voltageTopic() {
        return TopicBuilder.name("data-voltage")
                .partitions(5) //here should be an odd number of partitions to make kafka normally working
                .replicas(1)
                .config(
                        TopicConfig.RETENTION_MS_CONFIG,
                        String.valueOf(Duration.ofDays(7).toMillis()) // how long the data will be stored in partitions
                )
                .build();
    }

    @Bean//topic that will save and store the data from sensors by power
    public NewTopic powerTopic() {
        return TopicBuilder.name("data-power")
                .partitions(5) //here should be an odd number of partitions to make kafka normally working
                .replicas(1)
                .config(
                        TopicConfig.RETENTION_MS_CONFIG,
                        String.valueOf(Duration.ofDays(7).toMillis()) // how long the data will be stored in partitions
                )
                .build();
    }

    //kafka configuration, who will send the messages to kafka
    @Bean
    public SenderOptions<String, Object> senderOptions() {
        Map<String, Object> props = new HashMap<>(3); // here will be stored the configuration
        props.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                servers
        );
        props.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                new TextXPath(this.settings, "//keySerializer")
                        .toString()
        );
        props.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                new TextXPath(this.settings, "//valueSerializer")
                        .toString()
        );
        return SenderOptions.create(props);
    }

    @Bean
    public KafkaSender<String, Object> kafkaSender(SenderOptions<String, Object> senderOptions) {
        return KafkaSender.create(senderOptions);
    }

}
