package com.example.datageneratormicroservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class Data {

    private Long sensorId;
    private LocalDateTime timestamp; //time when the sensor sent an info
    private double measurement; // measurement that the sensor sent
    private MeasurementType measurementType;

    public enum MeasurementType {

        TEMPERATURE,
        VOLTAGE,
        POWER

    }

}
