package com.example.datageneratormicroservice.service.impl;

import com.example.datageneratormicroservice.model.Data;
import com.example.datageneratormicroservice.service.KafkaDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.kafka.sender.KafkaSender;

@Service
@RequiredArgsConstructor
public class KafkaDataServiceImpl implements KafkaDataService {
    private final KafkaSender<String, Object> kafkaSender;
    @Override
    public void send(Data data) {

    }
}
