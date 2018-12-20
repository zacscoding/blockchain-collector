package collector.ethereum.event.listener;

import collector.ethereum.event.EthereumPendingTxEvent;
import collector.ethereum.event.EthereumTxEvent;
import collector.ethereum.event.publisher.EthereumPendingTxPublisher;
import collector.ethereum.event.publisher.EthereumTransactionPublisher;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zacconding
 * @Date 2018-12-20
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j(topic = "listener")
@Component
public class EthereumTransactionListener {

    @Autowired
    public EthereumTransactionListener(EthereumPendingTxPublisher ethPendingTxPublisher,
        EthereumTransactionPublisher ethTxPublisher) {

        // register
        ethPendingTxPublisher.register(this);
        ethTxPublisher.register(this);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onPendingTx(EthereumPendingTxEvent pendingTxEvent) {
        log.info("## subscribe pending tx event. network : {} / node : {} / hash : {}"
            , pendingTxEvent.getNetworkName(), pendingTxEvent.getNodeName(), pendingTxEvent.getPendingTx().getHash());
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onTransaction(EthereumTxEvent txEvent) {
        log.info("## subscribe tx event. network : {} / node : {} / hash : {} / status : {}"
            , txEvent.getNetworkName(), txEvent.getEthereumNode().getNodeName()
            , txEvent.getTransaction().getHash(), txEvent.getTransactionReceipt().getStatus());
    }
}
