package collector.ethereum;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zacconding
 * @Date 2018-12-19
 * @GitHub : https://github.com/zacscoding
 */
@Getter
@Setter
public class EthereumNetwork {

    // ethereum network name
    private String networkName;
    // ethereum block time
    private long blockTime;
    // ethereum pending tx polling time
    private long pendingTxPollingInterval;
    // ethereum nodes
    private List<EthereumNode> nodes;
}
