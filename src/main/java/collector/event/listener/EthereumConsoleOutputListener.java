package collector.event.listener;

import collector.configuration.EthConfiguration;
import collector.elasticsearch.parser.EthElasticParser;
import collector.event.EthBlockEvent;
import collector.event.EthPendingTxEvent;
import collector.event.EthTxEvent;
import collector.event.publisher.EthBlockPublisher;
import collector.event.publisher.EthPendingTxPublisher;
import collector.event.publisher.EthTransactionPublisher;
import collector.util.ThreadUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
//@Component
//@ConditionalOnBean(value = EthConfiguration.class)
public class EthereumConsoleOutputListener {

    private ObjectMapper objectMapper;

    @Autowired
    public EthereumConsoleOutputListener(EthBlockPublisher blockPublisher,
        EthPendingTxPublisher pendingTxPublisher,
        EthTransactionPublisher transactionPublisher,
        ObjectMapper objectMapper) {

        blockPublisher.register(this);
        blockPublisher.register(pendingTxPublisher);
        blockPublisher.register(transactionPublisher);
        this.objectMapper = objectMapper;
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onBlock(EthBlockEvent blockEvent) throws Exception {
        if (blockEvent == null) {
            logger.warn("Receive null block event \n{}", ThreadUtil.getStackTraceString(-5));
            return;
        }

        logger.info("## receive block event. network : {} / node : {} / block : {} / # tx : {}"
            , blockEvent.getNetworkName(), blockEvent.getEthereumNode().getNodeName(), blockEvent.getBlock().getNumber()
            , blockEvent.getBlock().getTransactions().size());

        logger.info("Es parse test \n{}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
            EthElasticParser.INSTANCE.parseBlock(blockEvent.getBlock())
        ));
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onPendingTx(EthPendingTxEvent pendingTxEvent) {
        if (pendingTxEvent == null) {
            logger.warn("Receive null pending tx event \n{}", ThreadUtil.getStackTraceString(-5));
            return;
        }

        logger.info("## subscribe pending tx event. network : {} / node : {} / hash : {}"
            , pendingTxEvent.getNetworkName(), pendingTxEvent.getNodeName(), pendingTxEvent.getPendingTx().getHash());
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onTransaction(EthTxEvent txEvent) throws JsonProcessingException {
        if (txEvent == null) {
            logger.warn("Receive null tx event \n{}", ThreadUtil.getStackTraceString(-5));
            return;
        }

        logger.info("## subscribe tx event. network : {} / node : {} / hash : {} / status : {}"
            , txEvent.getNetworkName(), txEvent.getEthereumNode().getNodeName()
            , txEvent.getTransaction().getHash(), txEvent.getTransactionReceipt().getStatus());

        logger.info("Es parse test \n{}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
            EthElasticParser.INSTANCE.parseTransaction(txEvent.getTransaction(), txEvent.getTransactionReceipt()
                , txEvent.getBlockTimestamp())
        ));
    }
}
