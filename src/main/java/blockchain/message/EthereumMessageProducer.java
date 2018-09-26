package blockchain.message;

import blockchain.model.BlockchainNode;
import blockchain.observe.listener.EthereumListener;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.Transaction;

/**
 * @author zacconding
 * @Date 2018-09-26
 * @GitHub : https://github.com/zacscoding
 */
public class EthereumMessageProducer implements EthereumListener {

    @Override
    public void onBlock(BlockchainNode blockchainNode, Block block) {
        System.out.println(">> onBlock.. : " + blockchainNode);
        System.out.println(block);
    }

    @Override
    public void onPendingTransaction(BlockchainNode blockchainNode, Transaction transaction) {
        System.out.println(">> onPendingTransaction.. : " + blockchainNode);
        System.out.println(transaction);
    }
}