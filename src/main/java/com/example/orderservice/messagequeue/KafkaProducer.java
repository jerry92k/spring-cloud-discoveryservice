package com.example.orderservice.messagequeue;

import com.example.orderservice.dto.OrderDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducer {
    private KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public OrderDto send(String topic, OrderDto orderDto){
        ObjectMapper mapper=new ObjectMapper();
        String jsonString="";

        try{
            jsonString=mapper.writeValueAsString(orderDto);
        }catch (JsonProcessingException ex){
            ex.printStackTrace();
        }
        kafkaTemplate.send(topic,jsonString);
        log.info("kafka produce data from order msa :"+orderDto);

        return orderDto;
    }
}
