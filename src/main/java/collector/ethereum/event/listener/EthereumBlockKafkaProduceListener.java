package collector.ethereum.event.listener;

import collector.configuration.EthereumConfiguration;
import collector.ethereum.event.EthereumBlockEvent;
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

/**
 * @author zacconding
 */
@Slf4j(topic = "listener")
@Component
@ConditionalOnBean(value = EthereumConfiguration.class)
public class EthereumBlockKafkaProduceListener {

    private EthereumKafkaProducer ethKafkaProducer;
    private EthereumRpcServiceManager rpcServiceManager;
    private EthereumTransactionPublisher ethTransactionPublisher;

    @Autowired
    public EthereumBlockKafkaProduceListener(EthereumKafkaProducer ethKafkaProducer,
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
     * - produce kafka block message
     */
    @Subscribe
    @AllowConcurrentEvents
    public void onBlock(EthereumBlockEvent blockEvent) {
        if (blockEvent == null) {
            logger.warn("receive null block event");
            return;
        }

        logger.debug("## subscribe block event. network : {} / node : {} / block : {} / # tx : {}"
            , blockEvent.getNetworkName(), blockEvent.getEthereumNode().getNodeName(), blockEvent.getBlock().getNumber()
            , blockEvent.getBlock().getTransactions().size());

        ethKafkaProducer.produceEthereumBlockMessage(blockEvent);
    }
}