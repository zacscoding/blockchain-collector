package collector.observer;

import collector.network.ethereum.EthereumNetwork;
import collector.network.ethereum.EthereumNode;
import collector.configuration.EthConfiguration;
import collector.configuration.properties.EthProperties;
import collector.event.EthBlockEvent;
import collector.event.EthPendingTxEvent;
import collector.event.publisher.EthBlockPublisher;
import collector.event.publisher.EthPendingTxPublisher;
import collector.rpc.EthRpcServiceManager;
import java.util.List;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.Transaction;

/**
 * Register eth events filters and then publish events
 *
 * @author zacconding
 * @Date 2018-12-19
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j(topic = "observer")
@Component
@ConditionalOnBean(value = EthConfiguration.class)
public class EthObserver {

    private EthProperties ethProperties;
    private EthRpcServiceManager ethRpcServiceManager;
    private EthBlockPublisher ethBlockPublisher;
    private EthPendingTxPublisher ethPendingTxPublisher;

    @Autowired
    public EthObserver(EthProperties ethProperties, EthRpcServiceManager ethRpcServiceManager
        , EthBlockPublisher ethBlockPublisher, EthPendingTxPublisher ethPendingTxPublisher) {

        this.ethProperties = ethProperties;
        this.ethRpcServiceManager = ethRpcServiceManager;
        this.ethBlockPublisher = ethBlockPublisher;
        this.ethPendingTxPublisher = ethPendingTxPublisher;

        startEthereumFilters();
    }

    /**
     * Start block filter + pending tx filter
     */
    private void startEthereumFilters() {
        List<EthereumNetwork> networks = ethProperties.getNetworks();
        if (CollectionUtils.isEmpty(networks)) {
            logger.info("## Skip ethereum observer because empty ethereum networks");
            return;
        }

        for (EthereumNetwork network : networks) {
            List<EthereumNode> ethNodes = network.getNodes();

            if (CollectionUtils.isEmpty(ethNodes)) {
                logger.info("## Skip observe {} eth network. because empty nodes", network.getNetworkName());
                continue;
            }

            logger.debug("## Start to observe {} ethereum network. block time : {} / # nodes : {}"
                , network.getNetworkName(), network.getBlockTime(), network.getNodes().size());

            for (EthereumNode ethNode : ethNodes) {
                try {
                    boolean result = false;
                    if (!ethRpcServiceManager.hasBlockFilter(ethNode)) {
                        // block filter
                        startBlockFilter(network.getNetworkName(), network.getBlockTime(), ethNode);
                    }

                    if (!ethRpcServiceManager.hasPendingTxFilter(ethNode)) {
                        // pending tx observer
                        startPendingTxFilter(network.getNetworkName(), network.getBlockTime(),
                            network.getPendingTxPollingInterval(), ethNode);
                    }
                } catch (Exception e) {
                    logger.error("Exception occur while registering filters", e);
                    ethRpcServiceManager.cancelBlockFilter(ethNode);
                    ethRpcServiceManager.cancelPendingTxFilter(ethNode);
                }
            }
        }
    }

    private void startBlockFilter(String networkName, long blockTime, EthereumNode ethNode) {
        // block observer
        final Consumer<Block> onBlock = block -> {
            EthBlockEvent event = new EthBlockEvent(networkName, blockTime, ethNode, block);
            ethBlockPublisher.publish(event);
        };

        boolean result = ethRpcServiceManager.registerBlockFilter(ethNode, blockTime, onBlock);
        logger.debug("## Tried to register block filter : {} > {}", ethNode.getNodeName(), result);
    }

    private void startPendingTxFilter(String networkName, long blockTime, long pollingInterval, EthereumNode ethNode) {
        final Consumer<Transaction> onPendingTx = tx -> {
            EthPendingTxEvent event = new EthPendingTxEvent(networkName, ethNode.getNodeName(), tx);
            ethPendingTxPublisher.publish(event);
        };

        boolean result = ethRpcServiceManager.registerPendingTxFilter(ethNode, blockTime, pollingInterval, onPendingTx);
        logger.debug("## Tried to register pending tx filter : {} > {}", ethNode.getNodeName(), result);
    }
}
