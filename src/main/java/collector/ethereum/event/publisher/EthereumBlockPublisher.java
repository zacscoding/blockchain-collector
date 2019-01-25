package collector.ethereum.event.publisher;

import collector.ethereum.EthereumNode;
import collector.ethereum.configuration.EthereumConfiguration;
import collector.ethereum.event.EthereumBlockEvent;
import collector.ethereum.event.EthereumTxEvent;
import collector.ethereum.rpc.EthereumRpcServiceManager;
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
@ConditionalOnBean(value = EthereumConfiguration.class)
public class EthereumBlockPublisher {

    private EventBus asyncEventBus;
    private EthereumTransactionPublisher ethTransactionPublisher;
    private EthereumRpcServiceManager rpcServiceManager;

    @Autowired
    public EthereumBlockPublisher(EthereumTransactionPublisher ethTransactionPublisher,
        EthereumRpcServiceManager rpcServiceManager) {

        this.ethTransactionPublisher = ethTransactionPublisher;
        this.rpcServiceManager = rpcServiceManager;
        CollectorThreadFactory factory = new CollectorThreadFactory("block-publisher", true);
        this.asyncEventBus = new AsyncEventBus("block-event-bus", Executors.newCachedThreadPool(factory));
    }

    public void publish(final EthereumBlockEvent blockEvent) {
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

                EthereumTxEvent txEvent = new EthereumTxEvent();

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
