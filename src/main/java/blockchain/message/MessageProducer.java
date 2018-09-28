package blockchain.message;

import blockchain.message.wrapper.ethereum.EthereumBlockWrapper;
import blockchain.message.wrapper.ethereum.EthereumTransactionWrapper;

/**
 * @author zacconding
 * @Date 2018-09-29
 * @GitHub : https://github.com/zacscoding
 */
public interface MessageProducer {

    // tag :: ethereum
    void produceEthereumBlock(EthereumBlockWrapper ethereumBlockWrapper);

    void produceEthereumPendingTx(EthereumTransactionWrapper ethereumTransactionWrapper);
    // -- tag :: ethereum
}