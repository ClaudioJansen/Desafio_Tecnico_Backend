package org.voting.kafka;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VotingResultProducer {

    private static final Logger logger = LoggerFactory.getLogger(VotingResultProducer.class);
    private static final String TOPIC = "voting-results";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendVotingResult(String resultJson) {
        logger.info("Sending voting result to Kafka: {}", resultJson);
        kafkaTemplate.send(TOPIC, resultJson);
    }
}
