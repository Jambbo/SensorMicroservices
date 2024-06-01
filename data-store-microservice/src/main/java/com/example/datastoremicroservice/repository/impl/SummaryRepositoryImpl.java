package com.example.datastoremicroservice.repository.impl;

import com.example.datastoremicroservice.config.RedisSchema;
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
                    summary.addValue(mt,entry);
            });
        });

        return Optional.of(summary);
    }


}