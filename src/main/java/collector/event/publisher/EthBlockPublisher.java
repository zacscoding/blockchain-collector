package collector.event.publisher;

import collector.network.ethereum.EthereumNode;
import collector.configuration.EthConfiguration;
import collector.event.EthBlockEvent;
import collector.event.EthTxEvent;
import collector.rpc.EthRpcServiceManager;
import collector.util.CollectorThreadFactory;
import collector.util.ThreadUtil;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Async;

/**
 * Publish block event & transaction events
 *
 * @author zacconding
 * @Date 2018-12-19
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j(topic = "publisher")
@Component
@ConditionalOnBean(value = EthConfiguration.class)
public class EthBlockPublisher {

    private EventBus asyncEventBus;
    private EthTransactionPublisher ethTransactionPublisher;
    private EthRpcServiceManager rpcServiceManager;

    @Autowired
    public EthBlockPublisher(EthTransactionPublisher ethTransactionPublisher,
        EthRpcServiceManager rpcServiceManager) {

        this.ethTransactionPublisher = ethTransactionPublisher;
        this.rpcServiceManager = rpcServiceManager;
        CollectorThreadFactory factory = new CollectorThreadFactory("block-publisher", true);
        this.asyncEventBus = new AsyncEventBus("block-event-bus", Executors.newCachedThreadPool(factory));
    }

    public void publish(final EthBlockEvent blockEvent) {
        if (blockEvent == null) {
            logger.warn("Published null block event. stack trace : {}", ThreadUtil.getStackTraceString(0));
        }

        asyncEventBus.post(blockEvent);
        Async.run((Callable<Void>) () -> {
            // publish transaction event
            publishTransactions(
                blockEvent.getBlock(), blockEvent.getNetworkName(),
                blockEvent.getBlockTime(), blockEvent.getEthereumNode()
            );
            return null;
        });
    }

    public void register(Object listener) {
        Objects.requireNonNull(listener, "listener must be not null");
        asyncEventBus.register(listener);
        logger.debug("## Success to register listener : {} at Eth Block Publisher", listener.getClass().getSimpleName());
    }

    private Callable<Void> publishTransactions(Block block, String networkName, long blockTime, EthereumNode ethNode) {
        final Web3j web3j = rpcServiceManager.getOrCreateWeb3j(ethNode, blockTime);
        block.getTransactions().forEach(transactionResult -> {
            try {
                Transaction tx = (Transaction) transactionResult.get();
                TransactionReceipt tr = web3j.ethGetTransactionReceipt(tx.getHash())
                    .send()
                    .getTransactionReceipt()
                    .get();

                EthTxEvent txEvent = new EthTxEvent();

                txEvent.setNetworkName(networkName);
                txEvent.setEthereumNode(ethNode);
                txEvent.setTransaction(tx);
                txEvent.setTransactionReceipt(tr);

                ethTransactionPublisher.publish(txEvent);
            } catch (Exception e) {
                logger.warn("Failed to get transaction receipt", e);
            }
        });

        return null;
    }
}
