package collector.network.ethereum;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author zacconding
 * @Date 2018-12-19
 * @GitHub : https://github.com/zacscoding
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
