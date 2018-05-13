package org.blocksync.wrapper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.Transaction;

/**
 * @author zacconding
 * @Date 2018-05-13
 * @GitHub : https://github.com/zacscoding
 */
@Getter
@Setter
@ToString
public class BlockWrapper {

    private transient Block block;
    private String hash;
    private BigInteger number;
    private String parentHash;
    private String nonce;
    private String transactionsRoot;
    private String stateRoot;
    private String miner;
    private String difficulty;
    private String totalDifficulty;     // integer of the total difficulty of the chain until this block.
    private String size;                // integer the size of this block in bytes.
    private String gasLimit;            // the maximum gas allowed in this block.
    private String gasUsed;             // the total used gas by all transactions in this block.
    private long timestamp;             // the unix timestamp for when the block was collated
    private List<TransactionWrapper> transactions;

    public BlockWrapper() {
    }

    public BlockWrapper(Block block) {
        this.block = block;
        this.hash = block.getHash();
        this.number = block.getNumber();
        this.parentHash = block.getParentHash();
        this.nonce = block.getNonceRaw();
        this.transactionsRoot = block.getTransactionsRoot();
        this.stateRoot = block.getStateRoot();
        this.miner = block.getMiner();
        this.difficulty = block.getDifficultyRaw();
        this.totalDifficulty = block.getTotalDifficultyRaw();
        this.size = block.getSizeRaw();
        this.gasLimit = block.getGasLimitRaw();
        this.gasUsed = block.getGasUsedRaw();
        this.timestamp = block.getTimestamp().longValue();

        if (block.getTransactions().size() > 0) {
            transactions = new ArrayList<>(block.getTransactions().size());
            block.getTransactions().forEach(tx -> transactions.add(new TransactionWrapper((Transaction) tx.get())));
        } else {
            transactions = Collections.emptyList();
        }
    }
}
