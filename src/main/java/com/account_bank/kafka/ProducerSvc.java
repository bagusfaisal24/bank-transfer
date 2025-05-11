package com.account_bank.kafka;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProducerSvc {

    private static final Logger logger = LoggerFactory.getLogger(ProducerSvc.class);

    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendKafka(String message){
        logger.warn("message + " + message);
        kafkaTemplate.send(TopicConstant.PAYMENT, "msg", message);
    }
}
