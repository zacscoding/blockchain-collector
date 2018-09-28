package blockchain.message;

import blockchain.message.parser.BlockchainParser;
import blockchain.message.wrapper.ethereum.EthereumBlockWrapper;
import blockchain.message.wrapper.ethereum.EthereumTransactionWrapper;
import blockchain.model.BlockchainNode;
import blockchain.observe.listener.EthereumListener;
import blockchain.util.GsonUtil;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.Transaction;

/**
 * After listen ethereum block + transaction event, Produce below messages
 * 1) Blocks
 * 2) Pending transactions
 * 3) Transaction results
 *
 * @author zacconding
 * @Date 2018-09-26
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j(topic = "message")
public class EthereumMessageProducer implements EthereumListener {

    private ReentrantReadWriteLock lock;
    private MessageProducer messageProducer;
    private Set<String> pendingTxHashes;

    public EthereumMessageProducer(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
        this.pendingTxHashes = new HashSet<>();
        this.lock = new ReentrantReadWriteLock();
    }

    @Override
    public void onBlock(BlockchainNode blockchainNode, Block block) {
        if (blockchainNode == null || block == null) {
            log.warn("Empty blockchainNode or block on block event. blockchain node : " + blockchainNode + ", block : " + block);
            return;
        }

        log.debug("## receive block. number : {}, hash : {}, tx count : {}", block.getNumber(), block.getHash(), CollectionUtils.isEmpty(block.getTransactions()) ? 0 : block.getTransactions().size());

        messageProducer.produceEthereumBlock(BlockchainParser.EthereumParser.parseBlock(blockchainNode, block));
        // TODO :: handle transaction receipt
    }

    @Override
    public void onPendingTransaction(BlockchainNode blockchainNode, Transaction transaction) {
        if (blockchainNode == null || transaction == null) {
            log.warn("Empty blockchainNode or transaction on pending tx event. blockchain node : " + blockchainNode + ", tx : " + transaction);
            return;
        }

        log.debug("## receive pending tx. hash : {}", transaction.getHash());
        messageProducer.produceEthereumPendingTx(BlockchainParser.EthereumParser.parseTransaction(blockchainNode, transaction, null));
        // TODO :: handle transaction receipt
        /*try {
            pendingTxHashes.add(transaction.getHash());
        } finally {
            lock.writeLock().unlock();
        }*/
    }
}