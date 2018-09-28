package blockchain.message.wrapper.ethereum;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zacconding
 * @Date 2018-09-29
 * @GitHub : https://github.com/zacscoding
 */
@Getter
@Setter
public class EthereumBlockWrapper {

    // tag :: blockchain node
    private String nodeName;
    // -- tag :: blockchain node

    // tag :: ethereum spec
    private String number;
    private String hash;
    private String parentHash;
    private String nonce;
    private String sha3Uncles;
    private String logsBloom;
    private String transactionsRoot;
    private String stateRoot;
    private String receiptsRoot;
    private String author;
    private String miner;
    private String mixHash;
    private String difficulty;
    private String totalDifficulty;
    private String extraData;
    private String size;
    private String gasLimit;
    private String gasUsed;
    private String timestamp;
    private List<String> transactions;
    private List<String> uncles;
    private List<String> sealFields;
    // -- tag :: ethereum spec
}