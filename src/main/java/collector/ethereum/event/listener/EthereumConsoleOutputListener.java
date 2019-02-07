package collector.ethereum.event.listener;

import collector.configuration.EthereumConfiguration;
import collector.ethereum.event.EthereumBlockEvent;
import collector.ethereum.event.EthereumPendingTxEvent;
import collector.ethereum.event.EthereumTxEvent;
import collector.ethereum.event.publisher.EthereumBlockPublisher;
import collector.ethereum.event.publisher.EthereumPendingTxPublisher;
import collector.ethereum.event.publisher.EthereumTransactionPublisher;
import collector.util.ThreadUtil;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j(topic = "dev")
@Component
@ConditionalOnBean(value = EthereumConfiguration.class)
public class EthereumConsoleOutputListener {

    @Autowired
    public EthereumConsoleOutputListener(EthereumBlockPublisher blockPublisher,
        EthereumPendingTxPublisher pendingTxPublisher,
        EthereumTransactionPublisher transactionPublisher) {

        blockPublisher.register(this);
        blockPublisher.register(pendingTxPublisher);
        blockPublisher.register(transactionPublisher);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onBlock(EthereumBlockEvent blockEvent) {
        if (blockEvent == null) {
            logger.warn("Receive null block event \n{}", ThreadUtil.getStackTraceString(-5));
            return;
        }

        logger.info("## receive block event. network : {} / node : {} / block : {} / # tx : {}"
            , blockEvent.getNetworkName(), blockEvent.getEthereumNode().getNodeName(), blockEvent.getBlock().getNumber()
            , blockEvent.getBlock().getTransactions().size());
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onPendingTx(EthereumPendingTxEvent pendingTxEvent) {
        if (pendingTxEvent == null) {
            logger.warn("Receive null pending tx event \n{}", ThreadUtil.getStackTraceString(-5));
            return;
        }

        logger.info("## subscribe pending tx event. network : {} / node : {} / hash : {}"
            , pendingTxEvent.getNetworkName(), pendingTxEvent.getNodeName(), pendingTxEvent.getPendingTx().getHash());
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onTransaction(EthereumTxEvent txEvent) {
        if (txEvent == null) {
            logger.warn("Receive null tx event \n{}", ThreadUtil.getStackTraceString(-5));
            return;
        }

        logger.info("## subscribe tx event. network : {} / node : {} / hash : {} / status : {}"
            , txEvent.getNetworkName(), txEvent.getEthereumNode().getNodeName()
            , txEvent.getTransaction().getHash(), txEvent.getTransactionReceipt().getStatus());
    }
}
