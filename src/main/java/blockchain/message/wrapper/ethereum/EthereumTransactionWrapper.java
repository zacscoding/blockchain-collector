package blockchain.message.wrapper.ethereum;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zacconding
 * @Date 2018-09-29
 * @GitHub : https://github.com/zacscoding
 */
@Getter
@Setter
public class EthereumTransactionWrapper {

    // tag :: blockchain node
    private String nodeName;
    // -- tag :: blockchain node

    // tag :: transaction
    private String hash;
    private String nonce;
    private String blockHash;
    private String blockNumber;
    private String transactionIndex;
    private String from;
    private String to;
    private String value;
    private String gasPrice;
    private String gas;
    private String input;
    private String creates;
    private String publicKey;
    private String raw;
    private String r;
    private String s;
    private String v;
    // tag :: transaction

    // tag :: transaction result
    private String cumulativeGasUsed;
    private String status;
    // private List<Log> logs;
    private String logsBloom;
    // -- tag :: transaction result
}