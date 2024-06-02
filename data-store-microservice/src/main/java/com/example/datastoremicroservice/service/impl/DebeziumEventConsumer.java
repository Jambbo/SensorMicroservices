package com.example.datastoremicroservice.service.impl;

import com.example.datastoremicroservice.model.Data;
import com.example.datastoremicroservice.model.MeasurementType;
import com.example.datastoremicroservice.service.CDCEventConsumer;
import com.example.datastoremicroservice.service.SummaryService;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import com.google.gson.JsonObject;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
public class DebeziumEventConsumer implements CDCEventConsumer {

    private final SummaryService summaryService;
    @Override
    @KafkaListener(topics = "data")
    public void handle(String message) {
        try{
            JsonObject payload = JsonParser.parseString(message)
                    .getAsJsonObject()
                    .get("payload")
                    .getAsJsonObject();

            Data data = Data.builder()
                    .id(payload.get("id").getAsLong())
                    .sensorId(payload.get("sensor_id").getAsLong())
                    .timestamp(LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(
                                    payload.get("timestamp").getAsLong()/1000
                            ),
                            TimeZone.getDefault().toZoneId()
                    ))
                    .measurement(payload.get("measurement").getAsDouble())
                    .measurementType(MeasurementType.valueOf(payload.get("type").getAsString()))
                    .build();
            summaryService.handle(data);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
