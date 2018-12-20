package collector.ethereum.event.listener;

import collector.ethereum.EthereumNode;
import collector.ethereum.configuration.EthereumConfiguration;
import collector.ethereum.event.EthereumBlockEvent;
import collector.ethereum.event.EthereumTxEvent;
import collector.ethereum.event.publisher.EthereumBlockPublisher;
import collector.ethereum.event.publisher.EthereumTransactionPublisher;
import collector.ethereum.message.EthereumKafkaProducer;
import collector.ethereum.rpc.EthereumRpcServiceManager;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

/**
 * @author zacconding
 */
@Slf4j(topic = "listener")
@Component
@ConditionalOnBean(value = EthereumConfiguration.class)
public class EthereumBlockListener {

    private EthereumKafkaProducer ethKafkaProducer;
    private EthereumRpcServiceManager rpcServiceManager;
    private EthereumTransactionPublisher ethTransactionPublisher;

    @Autowired
    public EthereumBlockListener(EthereumKafkaProducer ethKafkaProducer,
        EthereumBlockPublisher ethBlockPublisher,
        EthereumTransactionPublisher ethTransactionPublisher,
        EthereumRpcServiceManager rpcServiceManager) {

        this.ethKafkaProducer = ethKafkaProducer;
        this.ethTransactionPublisher = ethTransactionPublisher;
        this.rpcServiceManager = rpcServiceManager;

        // register
        ethBlockPublisher.register(this);
    }

    /**
     * Handle block event
     * - produce block message
     * - publish transaction + transaction receipt event
     */
    @Subscribe
    @AllowConcurrentEvents
    public void onBlock(EthereumBlockEvent blockEvent) {
        if (blockEvent == null) {
            log.warn("receive null block event");
            return;
        }

        log.debug("## subscribe block event. network : {} / node : {} / block : {} / # tx : {}"
            , blockEvent.getNetworkName(), blockEvent.getEthereumNode().getNodeName(), blockEvent.getBlock().getNumber()
            , blockEvent.getBlock().getTransactions().size());

        Block block = blockEvent.getBlock();

        ethKafkaProducer.produceEthereumBlockMessage(blockEvent);
        if (CollectionUtils.isEmpty(block.getTransactions())) {
            return;
        }

        // publish transaction event
        publishTransactions(
            block, blockEvent.getNetworkName(), blockEvent.getBlockTime(), blockEvent.getEthereumNode()
        );
    }

    private void publishTransactions(Block block, String networkName, long blockTime, EthereumNode ethNode) {
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
                log.warn("Failed to get transaction receipt", e);
            }
        });
    }
}