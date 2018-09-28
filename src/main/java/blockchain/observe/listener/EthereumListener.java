package blockchain.observe.listener;

import blockchain.model.BlockchainNode;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.Transaction;

/**
 * Ethereum event listener
 *
 * @author zacconding
 * @Date 2018-09-26
 * @GitHub : https://github.com/zacscoding
 */
public interface EthereumListener {

    /**
     * Listen blocks with transactions
     */
    void onBlock(BlockchainNode blockchainNode, Block block);

    /**
     * Listen pending transactions
     */
    void onPendingTransaction(BlockchainNode blockchainNode, Transaction transaction);
}