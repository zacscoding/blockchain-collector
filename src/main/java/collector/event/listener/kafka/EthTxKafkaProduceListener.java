package collector.event.listener.kafka;

import collector.configuration.EthConfiguration;
import collector.configuration.EthKafkaConfiguration;
import collector.event.EthPendingTxEvent;
import collector.event.EthTxEvent;
import collector.event.publisher.EthPendingTxPublisher;
import collector.event.publisher.EthTransactionPublisher;
import collector.message.kafka.EthKafkaProducer;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * Produce kafka tx messages
 *
 * @author zacconding
 */
@Slf4j(topic = "listener")
@Component
@ConditionalOnBean(value = {EthConfiguration.class, EthKafkaConfiguration.class})
public class EthTxKafkaProduceListener {

    private EthKafkaProducer ethKafkaProducer;

    @Autowired
    public EthTxKafkaProduceListener(EthKafkaProducer ethKafkaProducer,
        EthPendingTxPublisher ethPendingTxPublisher,
        EthTransactionPublisher ethTxPublisher) {

        this.ethKafkaProducer = ethKafkaProducer;

        // register
        ethPendingTxPublisher.register(this);
        ethTxPublisher.register(this);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onPendingTx(EthPendingTxEvent pendingTxEvent) {
        logger.info("## subscribe pending tx event. network : {} / node : {} / hash : {}"
            , pendingTxEvent.getNetworkName(), pendingTxEvent.getNodeName(), pendingTxEvent.getPendingTx().getHash());

        ethKafkaProducer.produceEthereumPendingTxMessage(pendingTxEvent);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onTransaction(EthTxEvent txEvent) {
        logger.info("## subscribe tx event. network : {} / node : {} / hash : {} / status : {}"
            , txEvent.getNetworkName(), txEvent.getEthereumNode().getNodeName()
            , txEvent.getTransaction().getHash(), txEvent.getTransactionReceipt().getStatus());

        ethKafkaProducer.produceEthereumTransactionMessage(txEvent);
    }
}
