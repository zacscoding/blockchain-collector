package blockchain.observe.listener;

import blockchain.model.BlockchainNode;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.Transaction;

/**
 * @author zacconding
 * @Date 2018-09-26
 * @GitHub : https://github.com/zacscoding
 */
public interface EthereumListener {

    void onBlock(BlockchainNode blockchainNode, Block block);

    void onPendingTransaction(BlockchainNode blockchainNode, Transaction transaction);
}