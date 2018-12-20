package collector.ethereum.event.listener;

import collector.ethereum.configuration.EthereumConfiguration;
import collector.ethereum.event.EthereumPendingTxEvent;
import collector.ethereum.event.EthereumTxEvent;
import collector.ethereum.event.publisher.EthereumPendingTxPublisher;
import collector.ethereum.event.publisher.EthereumTransactionPublisher;
import collector.ethereum.message.EthereumKafkaProducer;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * @author zacconding
 */
@Slf4j(topic = "listener")
@Component
@ConditionalOnBean(value = EthereumConfiguration.class)
public class EthereumTransactionListener {

    private EthereumKafkaProducer ethKafkaProducer;

    @Autowired
    public EthereumTransactionListener(EthereumKafkaProducer ethKafkaProducer,
        EthereumPendingTxPublisher ethPendingTxPublisher,
        EthereumTransactionPublisher ethTxPublisher) {

        this.ethKafkaProducer = ethKafkaProducer;

        // register
        ethPendingTxPublisher.register(this);
        ethTxPublisher.register(this);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onPendingTx(EthereumPendingTxEvent pendingTxEvent) {
        log.info("## subscribe pending tx event. network : {} / node : {} / hash : {}"
            , pendingTxEvent.getNetworkName(), pendingTxEvent.getNodeName(), pendingTxEvent.getPendingTx().getHash());

        ethKafkaProducer.produceEthereumPendingTxMessage(pendingTxEvent);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onTransaction(EthereumTxEvent txEvent) {
        log.info("## subscribe tx event. network : {} / node : {} / hash : {} / status : {}"
            , txEvent.getNetworkName(), txEvent.getEthereumNode().getNodeName()
            , txEvent.getTransaction().getHash(), txEvent.getTransactionReceipt().getStatus());

        ethKafkaProducer.produceEthereumTransactionMessage(txEvent);
    }
}
