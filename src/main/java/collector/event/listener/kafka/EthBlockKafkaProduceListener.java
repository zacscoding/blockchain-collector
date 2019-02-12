package collector.event.listener.kafka;

import collector.configuration.EthConfiguration;
import collector.configuration.EthKafkaConfiguration;
import collector.event.EthBlockEvent;
import collector.event.publisher.EthBlockPublisher;
import collector.event.publisher.EthTransactionPublisher;
import collector.message.kafka.EthKafkaProducer;
import collector.rpc.EthRpcServiceManager;
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
@ConditionalOnBean(value = {EthConfiguration.class, EthKafkaConfiguration.class})
@Component
public class EthBlockKafkaProduceListener {

    private EthKafkaProducer ethKafkaProducer;
    private EthRpcServiceManager rpcServiceManager;
    private EthTransactionPublisher ethTransactionPublisher;

    @Autowired
    public EthBlockKafkaProduceListener(EthKafkaProducer ethKafkaProducer,
        EthBlockPublisher ethBlockPublisher,
        EthTransactionPublisher ethTransactionPublisher,
        EthRpcServiceManager rpcServiceManager) {

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
    public void onBlock(EthBlockEvent blockEvent) {
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
