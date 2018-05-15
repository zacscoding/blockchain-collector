package org.blocksync.handler;

import org.blocksync.entity.Node;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;

/**
 * @author zacconding
 * @Date 2018-05-15
 * @GitHub : https://github.com/zacscoding
 */
public class PendingManageEventHandler extends BlockEventHandlerAdapter {

    @Override
    public void onBlock(Node node, EthBlock ethBlock) {

    }

    @Override
    public void onPendingTransaction(Node node, Transaction tx) {

    }
}
