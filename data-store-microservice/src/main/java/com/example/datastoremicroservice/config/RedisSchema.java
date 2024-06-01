package com.example.datastoremicroservice.config;

import com.example.datastoremicroservice.model.MeasurementType;

//class that describes how the keys will be named
public class RedisSchema {

    //set
    public static String sensorKeys(){
        return KeyHelper.getKey("sensors");
    }

    //hash with summary types
    public static String summaryKey(
            Long sensorId, MeasurementType measurementType
    ){
        return KeyHelper.getKey("sensors:"+sensorId+":"+measurementType.name().toLowerCase());
    }

}
