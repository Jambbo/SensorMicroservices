package com.example.datageneratormicroservice.service.impl;

import com.example.datageneratormicroservice.model.Data;
import com.example.datageneratormicroservice.service.KafkaDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Service
@RequiredArgsConstructor
public class KafkaDataServiceImpl implements KafkaDataService {
    private final KafkaSender<String, Object> kafkaSender;

    @Override
    public void send(Data data) {
        String topic = switch (data.getMeasurementType()) {
            case TEMPERATURE -> "data-temperature";
            case VOLTAGE -> "data-voltage";
            case POWER -> "data-power";
        };
        kafkaSender.send(
                        //Mono - object, that encapsulate the data inside the object.
                        // Mono object is used to send data in reactive approach
                        Mono.just(
                                SenderRecord.create(
                                        topic,
                                        0,
                                        System.currentTimeMillis(),
  /*hashcode is a key this time*/       String.valueOf(data.hashCode()),
                                        data,
                                        null
                                )
                        )
                )
                .subscribe();
    }
}
