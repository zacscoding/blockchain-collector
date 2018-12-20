package collector.ethereum.message;

import collector.ethereum.configuration.EthereumConfiguration;
import collector.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Test for produced messages
 *
 * @author zacconding
 * @Date 2018-12-20
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j(topic = "message")
@Component
@ConditionalOnBean(value = EthereumConfiguration.class)
public class EthereumKafkaConsumerForDev {

    @KafkaListener(topics = "#{ethereumKafkaProperties.topic.block}", groupId = "1", containerFactory = "kafkaListenerContainerFactory")
    public void onBlock(@Payload String message) {
        printMessageLog("## [Consumer] receive block message.", message);
    }

    @KafkaListener(topics = "#{ethereumKafkaProperties.topic.tx}", groupId = "1", containerFactory = "kafkaListenerContainerFactory")
    public void onTx(@Payload String message) {
        printMessageLog("## [Consumer] receive tx message.", message);
    }

    @KafkaListener(topics = "#{ethereumKafkaProperties.topic.pendingTx}", groupId = "1", containerFactory = "kafkaListenerContainerFactory")
    public void onPendingTx(@Payload String message) {
        printMessageLog("## [Consumer] receive pending message.", message);
    }

    private void printMessageLog(String prefix, String message) {
        log.info(prefix + "\n" + GsonUtil.jsonStringToPretty(message));
    }
}