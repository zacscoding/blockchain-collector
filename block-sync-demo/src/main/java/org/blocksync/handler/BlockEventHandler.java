package org.blocksync.handler;

import org.blocksync.entity.Node;
import org.web3j.protocol.core.methods.response.EthBlock;

/**
 * @author zacconding
 * @Date 2018-05-12
 * @GitHub : https://github.com/zacscoding
 */
public interface BlockEventHandler {

    void onBlock(Node node, EthBlock ethBlock);
}
