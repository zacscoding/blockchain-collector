package collector.message;

import collector.event.EthBlockEvent;
import collector.event.EthPendingTxEvent;
import collector.event.EthTxEvent;

/**
 * Test for produced messages
 *
 * @author zacconding
 * @Date 2018-12-20
 * @GitHub : https://github.com/zacscoding
 */
public interface EthMessageConverter {

    /**
     * Convert block event to string message
     */
    String convertBlockEvent(EthBlockEvent blockEvent);

    /**
     * Convert tx event to string message
     */
    String convertTxEvent(EthTxEvent txEvent);

    /**
     * Convert pending tx event to string message
     */
    String convertPendingTxEvent(EthPendingTxEvent pendingTxEvent);
}