package com.example.datageneratormicroservice.web.dto;

import com.example.datageneratormicroservice.model.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class DataDto {

    private Long sensorId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // format of received info from sensors
    private LocalDateTime timestamp; //time when the sensor sent an info

    private double measurement; // measurement that the sensor sent
    private Data.MeasurementType measurementType;

}
