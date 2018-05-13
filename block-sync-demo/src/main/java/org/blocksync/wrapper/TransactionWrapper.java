package org.blocksync.wrapper;


import java.math.BigInteger;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.web3j.protocol.core.methods.response.Transaction;

/**
 * @author zacconding
 * @Date 2018-05-01
 * @GitHub : https://github.com/zacscoding
 */
@Getter
@Setter
@ToString
public class TransactionWrapper {
    private transient Transaction transaction;

    private String hash;
    private String nonce;
    private String blockHash;
    private BigInteger blockNumber;
    private BigInteger transactionIndex;
    private String from;
    private String to;
    private String value;
    private String gasPrice;
    private String gas;

    public TransactionWrapper() {
    }

    public TransactionWrapper(Transaction tx) {
        this.transaction = tx;
        this.hash = tx.getHash();
        this.nonce = tx.getNonceRaw();
        this.blockHash = tx.getBlockHash();
        this.blockNumber = tx.getBlockNumber();
        this.transactionIndex = tx.getTransactionIndex();
        this.from = tx.getFrom();
        this.to = tx.getTo();
        this.value = tx.getValueRaw();
        this.gasPrice = tx.getGasPriceRaw();
        this.gas = tx.getGasRaw();
    }
}