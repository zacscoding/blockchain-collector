package collector.ethereum.observer;

import collector.configuration.EthereumProperties;
import collector.ethereum.EthereumNetwork;
import collector.ethereum.EthereumNode;
import collector.ethereum.rpc.EthereumRpcServiceManager;
import collector.event.EthereumBlockEvent;
import collector.event.publisher.EthereumBlockPublisher;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.web3j.protocol.core.methods.response.EthBlock.Block;

/**
 * @author zacconding
 * @Date 2018-12-19
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j(topic = "observer")
@Component
public class EthereumObserver {

    private EthereumProperties ethProperties;
    private EthereumBlockPublisher ethBlockPublisher;
    private EthereumRpcServiceManager ethRpcServiceManager;

    @Autowired
    public EthereumObserver(EthereumProperties ethProperties, EthereumBlockPublisher ethBlockPublisher,
        EthereumRpcServiceManager ethRpcServiceManager) {

        this.ethProperties = ethProperties;
        this.ethBlockPublisher = ethBlockPublisher;
        this.ethRpcServiceManager = ethRpcServiceManager;
    }

    @PostConstruct
    private void setUp() {
        startObserve();
    }

    private void startObserve() {
        List<EthereumNetwork> networks = ethProperties.getNetworks();
        if (CollectionUtils.isEmpty(networks)) {
            log.info("## Skip ethereum observer because empty ethereum networks");
            return;
        }

        for (EthereumNetwork network : networks) {
            List<EthereumNode> ethNodes = network.getNodes();

            if (CollectionUtils.isEmpty(ethNodes)) {
                log.info("## Skip observe {} eth network. because empty nodes", network.getNetworkName());
                continue;
            }

            log.debug("## Start to observe {} ethereum network. block time : {} / # nodes : {}"
                , network.getNetworkName(), network.getBlockTime(), network.getNodes().size());

            for (EthereumNode ethNode : ethNodes) {
                // block observer
                final Consumer<Block> onBlock = block -> {
                    EthereumBlockEvent blockEvent = new EthereumBlockEvent(network.getNetworkName(),
                        ethNode.getNodeName(), block);
                    ethBlockPublisher.publish(blockEvent);
                };

                boolean result = ethRpcServiceManager.registerBlockFilter(ethNode, network.getBlockTime(), onBlock);
                log.debug("## Tried to register block filter : {} > {}", ethNode.getNodeName(), result);

                // pending tx observer
            }
        }
    }
}
