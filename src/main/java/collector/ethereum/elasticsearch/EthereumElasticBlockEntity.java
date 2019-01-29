package collector.ethereum.elasticsearch;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

/**
 * Ethereum block entity
 *
 * @GitHub : https://github.com/zacscoding
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Mapping(mappingPath = "/elasticsearch/mappings/ethereum-blocks.json")
@Setting(settingPath = "/elasticsearch/mappings/ethereum-blocks.json")
public class EthereumElasticBlockEntity {

    @Id
    private String hash;                // 32 bytes hash of the block
    private long number;                // block number
    private String parentHash;          // 32 Bytes - hash of the parent block
    private String nonce;               // 8 Bytes - hash of the generated proof-of-work
    private String sha3Uncles;          // 32 Bytes - SHA3 of the uncles data in the block.
    private String miner;               // 20 Bytes - the address of the beneficiary to whom the mining rewards were given.
    private String difficulty;          // integer of the difficulty for this block.
    private String extraData;           // the "extra data" field of this block.
    private String size;                // integer the size of this block in bytes.
    private String gasLimit;            // the maximum gas allowed in this block.
    private String gasUsed;             // the total used gas by all transactions in this block.
    private long timestamp;             // the unix timestamp for when the block was collated
    private String receiptRoot;
    private String stateRoot;
    private String transactionsRoot;
    private String logsBloom;
    private String mixHash;
    private String txFees;
    private List<String> transactions;  // transactions hash list
    private List<String> uncles;        // uncles`s hash list
    private List<String> sealFields;    // seal fields
    private int txCount;
}