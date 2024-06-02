package com.example.datastoremicroservice.model;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class Data {

    private Long id;
    private Long sensorId;
    private LocalDateTime timestamp;
    private Double measurement;
    private MeasurementType measurementType;

}
