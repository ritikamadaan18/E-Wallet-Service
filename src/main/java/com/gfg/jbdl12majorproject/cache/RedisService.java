package com.gfg.jbdl12majorproject.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
    @Autowired
    private RedisRepository redisRepository;
    ObjectMapper objectMapper = new ObjectMapper();
    @KafkaListener(topics = {"lastTransaction"}, groupId = "RService")
    public void insertRatingToRedis(String input) throws JsonProcessingException {
        RedisEntity rating = objectMapper.readValue(input,RedisEntity.class);
        redisRepository.save(rating);
    }
}
