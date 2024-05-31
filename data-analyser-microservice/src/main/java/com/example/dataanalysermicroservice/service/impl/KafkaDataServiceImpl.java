package com.example.dataanalysermicroservice.service.impl;

import com.example.dataanalysermicroservice.model.Data;
import com.example.dataanalysermicroservice.service.KafkaDataService;
import org.springframework.stereotype.Service;

@Service
public class KafkaDataServiceImpl implements KafkaDataService {
    @Override
    public void handle(Data data) {
        System.out.println("Data object is received: "+data.toString());
    }
}
