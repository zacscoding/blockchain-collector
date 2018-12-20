package collector.ethereum.message;

import collector.ethereum.event.EthereumBlockEvent;
import collector.ethereum.event.EthereumPendingTxEvent;
import collector.ethereum.event.EthereumTxEvent;

/**
 * Test for produced messages
 *
 * @author zacconding
 * @Date 2018-12-20
 * @GitHub : https://github.com/zacscoding
 */
public interface EthereumMessageConverter {

    /**
     * Convert block event to string message
     */
    String convertBlockEvent(EthereumBlockEvent blockEvent);

    /**
     * Convert tx event to string message
     */
    String convertTxEvent(EthereumTxEvent txEvent);

    /**
     * Convert pending tx event to string message
     */
    String convertPendingTxEvent(EthereumPendingTxEvent pendingTxEvent);
}