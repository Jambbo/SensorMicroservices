package com.example.datageneratormicroservice.config;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.sender.KafkaSender;

import java.io.File;
import java.io.FileNotFoundException;

@Configuration
public class BeanConfig {

    @SneakyThrows
    @Bean
    public XML producerXML(){
        return new XMLDocument(
                new File("src/main/resources/kafka/producer.xml")
        );
    }

}
