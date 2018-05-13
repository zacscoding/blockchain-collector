package org.observer.handler;

import org.slf4j.Logger;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock.Block;

/**
 * @author zacconding
 * @Date 2018-05-03
 * @GitHub : https://github.com/zacscoding
 */
public interface BlockEventHandler {

    void handleBlock(String url, Block block, Web3j web3j);

    void handleError(Throwable t);

    default void commonHandleError(Logger logger, Throwable t) {
        logger.error("Failed to subscribe block..", t);
    }
}
