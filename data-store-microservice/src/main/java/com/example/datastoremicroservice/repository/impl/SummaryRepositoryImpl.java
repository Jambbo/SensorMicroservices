package com.example.datastoremicroservice.repository.impl;

import com.example.datastoremicroservice.config.RedisSchema;
import com.example.datastoremicroservice.model.Data;
import com.example.datastoremicroservice.model.MeasurementType;
import com.example.datastoremicroservice.model.Summary;
import com.example.datastoremicroservice.model.SummaryType;
import com.example.datastoremicroservice.repository.SummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Optional;
import java.util.Set;

import static java.util.Objects.isNull;

@Repository
@RequiredArgsConstructor
public class SummaryRepositoryImpl implements SummaryRepository {

    private final JedisPool jedisPool; //to work with 8 threads. Multithreading treatment to redis

    @Override
    public Optional<Summary> findBySensorId(
                                            Long sensorId,
                                            Set<MeasurementType> measurementTypes,
                                            Set<SummaryType> summaryTypes
    ) {
        //As we r working with a pool => it is autocloseable

        try (Jedis jedis = jedisPool.getResource()) {
            if (!jedis.sismember(
                    RedisSchema.sensorKeys(),
                    String.valueOf(sensorId)
            )) {
                return Optional.empty();
            }
            if (measurementTypes.isEmpty() && !summaryTypes.isEmpty()) {
                return getSummary(
                        sensorId,
                        Set.of(MeasurementType.values()),
                        summaryTypes,
                        jedis
                );
            } else if (!measurementTypes.isEmpty() && summaryTypes.isEmpty()) {
                return getSummary(
                        sensorId,
                        measurementTypes,
                        Set.of(SummaryType.values()),
                        jedis
                );
            } else {
                return getSummary(
                        sensorId,
                        measurementTypes,
                        summaryTypes,
                        jedis
                );
            }
        }
    }

    @Override
    public void handle(Data data) {
        try(Jedis jedis = jedisPool.getResource()){
            if(!jedis.sismember(
                    RedisSchema.sensorKeys(),
                    String.valueOf(data.getSensorId())
            )){
                jedis.sadd(
                        RedisSchema.sensorKeys(),
                        String.valueOf(data.getSensorId())
                );
            }
            updateMinValue(data, jedis);
            updateMaxValue(data, jedis);
            updateSumAndAvgValue(data, jedis);

        }catch(Exception e){

        }
    }

    private void updateMinValue(Data data, Jedis jedis) {
        String minTypeField = SummaryType.MIN.name().toLowerCase();
        Double measurement = data.getMeasurement();
        String key = RedisSchema.summaryKey(
                data.getSensorId(),data.getMeasurementType()
        );//key where we store data
        String value = jedis.hget(key,minTypeField);
        if(isNull(value) || measurement<Double.parseDouble(value)){
            jedis.hset(key,minTypeField,String.valueOf(measurement));
        }
    }

    private void updateMaxValue(Data data, Jedis jedis) {
        String maxTypeField = SummaryType.MAX.name().toLowerCase();
        Double measurement = data.getMeasurement();
        String key = RedisSchema.summaryKey(
                data.getSensorId(),data.getMeasurementType()
        );//key where we store data
        String value = jedis.hget(key,maxTypeField);
        if(isNull(value) || measurement>Double.parseDouble(value)){
            jedis.hset(key,maxTypeField,String.valueOf(measurement));
        }
    }

    private void updateSumAndAvgValue(Data data, Jedis jedis) {
        updateSumValue(data, jedis);
        String key = RedisSchema.summaryKey(
                data.getSensorId(),data.getMeasurementType()
        );
        String counter = jedis.hget(key,"counter");
        if(isNull(counter)){
            counter = String.valueOf(jedis.hset(key,"counter",String.valueOf(1)));
        }else{
            counter = String.valueOf(jedis.hincrBy(key,"counter",1));
        }
        String sum = jedis.hget(key,SummaryType.SUM.name().toLowerCase());
        jedis.hset(
                key,
                SummaryType.AVG.name().toLowerCase(),
                String.valueOf(
                        Double.parseDouble(sum)/Double.parseDouble(counter)
                )
        );
    }

    private void updateSumValue(Data data, Jedis jedis) {
        String sumTypeField = SummaryType.SUM.name().toLowerCase();
        Double measurement = data.getMeasurement();
        String key = RedisSchema.summaryKey(
                data.getSensorId(),data.getMeasurementType()
        );
        String value = jedis.hget(key,sumTypeField);
        if(isNull(value)){
            jedis.hset(key,sumTypeField,String.valueOf(measurement));
        }else{
            jedis.hincrByFloat(key,sumTypeField,measurement);
        }
    }

    private Optional<Summary> getSummary(
            Long sensorId,
            Set<MeasurementType> measurementTypes,
            Set<SummaryType> summaryTypes,
            Jedis jedis
    ) {
        Summary summary = new Summary();
        summary.setSensorId(sensorId);
        measurementTypes.forEach(mt -> {
            summaryTypes.forEach(st -> {
                Summary.SummaryEntry entry = new Summary.SummaryEntry();
                    entry.setType(st);
                    String value = jedis.hget(
                            RedisSchema.summaryKey(sensorId,mt),
                            st.name().toLowerCase()
                    );
                    if(value!=null){
                        entry.setValue(Double.parseDouble(value));
                    }
                    String counter = jedis.hget(
                            RedisSchema.summaryKey(sensorId,mt),
                            "counter"
                    );
                    if(counter!=null){
                        entry.setCounter(Long.parseLong(counter));
                    }
                    summary.addValue(mt,entry);
            });
        });

        return Optional.of(summary);
    }


}
