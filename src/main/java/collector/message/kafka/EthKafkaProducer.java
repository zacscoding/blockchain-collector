package collector.message.kafka;

import collector.configuration.EthKafkaConfiguration;
import collector.configuration.properties.EthKafkaProperties;
import collector.event.EthBlockEvent;
import collector.event.EthEvent;
import collector.event.EthPendingTxEvent;
import collector.event.EthTxEvent;
import collector.message.EthMessageConverter;
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
@ConditionalOnBean(value = {EthKafkaConfiguration.class})
public class EthKafkaProducer {

    private EthMessageConverter messageConverter;
    private KafkaTemplate<String, String> kafkaTemplate;
    private String blockTopic;
    private String txTopic;
    private String pendingTxTopic;

    public EthKafkaProducer(EthMessageConverter messageConverter,
        KafkaTemplate<String, String> kafkaTemplate,
        EthKafkaProperties ethKafkaProperties) {

        this.messageConverter = messageConverter;
        this.kafkaTemplate = kafkaTemplate;

        this.blockTopic = ethKafkaProperties.getTopic().getBlock();
        this.txTopic = ethKafkaProperties.getTopic().getTx();
        this.pendingTxTopic = ethKafkaProperties.getTopic().getPendingTx();
    }

    /**
     * Produce block message
     */
    public void produceEthereumBlockMessage(EthBlockEvent blockEvent) {
        String message = messageConverter.convertBlockEvent(blockEvent);

        ProducerRecord<String, String> record = new ProducerRecord<>(blockTopic, message);
        ListenableFuture<SendResult<String, String>> result = kafkaTemplate.send(record);

        addPrintLogCallback(blockEvent, result);
    }

    /**
     * Produce transaction message
     */
    public void produceEthereumTransactionMessage(EthTxEvent txEvent) {
        String message = messageConverter.convertTxEvent(txEvent);

        ProducerRecord<String, String> record = new ProducerRecord<>(txTopic, message);
        ListenableFuture<SendResult<String, String>> result = kafkaTemplate.send(record);

        addPrintLogCallback(txEvent, result);
    }

    /**
     * Produce pending transaction message
     */
    public void produceEthereumPendingTxMessage(EthPendingTxEvent pendingTxEvent) {
        String message = messageConverter.convertPendingTxEvent(pendingTxEvent);

        ProducerRecord<String, String> record = new ProducerRecord<>(pendingTxTopic, message);
        ListenableFuture<SendResult<String, String>> result = kafkaTemplate.send(record);

        addPrintLogCallback(pendingTxEvent, result);
    }

    /**
     * Print result of producing message
     */
    private void addPrintLogCallback(EthEvent event, ListenableFuture<SendResult<String, String>> result) {
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