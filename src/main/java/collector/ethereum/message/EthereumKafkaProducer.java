package collector.ethereum.message;

import collector.configuration.EthereumConfiguration;
import collector.configuration.EthereumKafkaConfiguration;
import collector.configuration.properties.EthereumKafkaProperties;
import collector.ethereum.event.EthereumBlockEvent;
import collector.ethereum.event.EthereumEvent;
import collector.ethereum.event.EthereumPendingTxEvent;
import collector.ethereum.event.EthereumTxEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * Produce ethereum event messages
 *
 * @author zacconding
 * @Date 2018-12-20
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j(topic = "message")
@Component
@ConditionalOnBean(value = {EthereumKafkaConfiguration.class})
public class EthereumKafkaProducer {

    private EthereumMessageConverter messageConverter;
    private KafkaTemplate<String, String> kafkaTemplate;
    private String blockTopic;
    private String txTopic;
    private String pendingTxTopic;

    public EthereumKafkaProducer(EthereumMessageConverter messageConverter,
        KafkaTemplate<String, String> kafkaTemplate,
        EthereumKafkaProperties ethKafkaProperties) {

        this.messageConverter = messageConverter;
        this.kafkaTemplate = kafkaTemplate;

        this.blockTopic = ethKafkaProperties.getTopic().getBlock();
        this.txTopic = ethKafkaProperties.getTopic().getTx();
        this.pendingTxTopic = ethKafkaProperties.getTopic().getPendingTx();
    }

    /**
     * Produce block message
     */
    public void produceEthereumBlockMessage(EthereumBlockEvent blockEvent) {
        String message = messageConverter.convertBlockEvent(blockEvent);

        ProducerRecord<String, String> record = new ProducerRecord<>(blockTopic, message);
        ListenableFuture<SendResult<String, String>> result = kafkaTemplate.send(record);

        addPrintLogCallback(blockEvent, result);
    }

    /**
     * Produce transaction message
     */
    public void produceEthereumTransactionMessage(EthereumTxEvent txEvent) {
        String message = messageConverter.convertTxEvent(txEvent);

        ProducerRecord<String, String> record = new ProducerRecord<>(txTopic, message);
        ListenableFuture<SendResult<String, String>> result = kafkaTemplate.send(record);

        addPrintLogCallback(txEvent, result);
    }

    /**
     * Produce pending transaction message
     */
    public void produceEthereumPendingTxMessage(EthereumPendingTxEvent pendingTxEvent) {
        String message = messageConverter.convertPendingTxEvent(pendingTxEvent);

        ProducerRecord<String, String> record = new ProducerRecord<>(pendingTxTopic, message);
        ListenableFuture<SendResult<String, String>> result = kafkaTemplate.send(record);

        addPrintLogCallback(pendingTxEvent, result);
    }

    /**
     * Print result of producing message
     */
    private void addPrintLogCallback(EthereumEvent event, ListenableFuture<SendResult<String, String>> result) {
        result.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                logger.warn("Failed to send message {}. reason : {}"
                    , event.toSimpleString(), throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, String> sendResult) {
                logger.debug("Success to produce message : {}", event.toSimpleString());
            }
        });
    }
}