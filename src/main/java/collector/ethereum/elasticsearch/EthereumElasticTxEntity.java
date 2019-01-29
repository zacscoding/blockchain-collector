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
 * Ethereum transaction entity
 *
 * @GitHub : https://github.com/zacscoding
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Mapping(mappingPath = "/elasticsearch/mappings/ethereum-transactions.json")
@Setting(settingPath = "/elasticsearch/mappings/ethereum-transactions.json")
public class EthereumElasticTxEntity {

    @Id
    private String hash;                    // 32 Bytes - hash of the transaction
    private String nonce;                   // the number of transactions made by the sender prior to this one
    private String blockHash;               // 32 Bytes - hash of the block where this transaction was in
    private long blockNumber;               // block number where this transaction was in
    private int transactionIndex;           // integer of the transactions index position in the block
    private String from;                    // 20 Bytes - address of the sender (Berith의 경우 파싱 해서 길이 달라짐)
    private String to;                      // 20 Bytes - address of the receiver (Berith의 경우 파싱 해서 길이 달라짐)
    private String value;                   // value transferred in Eth
    private String gasPrice;                // gas price provided by the sender in GWei
    private String gas;                     // gas provided by the sender
    private String txPrice;                 // transaction fee : (gas * gasPrice) / 1000000000000000000 ETH, where gas unit is WEI
    private String input;                   // the data sent along with the transaction
    private long timestamp;                 // tx timestamp(==block timestamp)

    /*  added from transaction receipt   */
    private String cumulativeGasUsed;       // The total amount of gas used when this transaction was executed in the block.
    private String contractAddress;         // 20 Bytes - The contract address created, if the transaction was a contract creation, otherwise null.
    private String status;                  // '0x0' indicates transaction failure , '0x1' indicates transaction succeeded.
    private List<EthereumElasticLogEntity> logs;
}