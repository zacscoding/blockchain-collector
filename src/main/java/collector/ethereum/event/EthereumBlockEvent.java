package collector.ethereum.event;

import collector.ethereum.EthereumNode;
import lombok.Getter;
import lombok.Setter;
import org.web3j.protocol.core.methods.response.EthBlock.Block;

/**
 * @author zacconding
 * @Date 2018-12-20
 * @GitHub : https://github.com/zacscoding
 */
@Getter
@Setter
public class EthereumBlockEvent extends EthereumEvent {

    private String networkName;
    private long blockTime;
    private EthereumNode ethereumNode;
    private Block block;

    public EthereumBlockEvent(String networkName, long blockTIme, EthereumNode ethereumNode, Block block) {
        super(EthereumEventType.BLOCK);

        this.networkName = networkName;
        this.blockTime = blockTIme;
        this.ethereumNode = ethereumNode;
        this.block = block;
    }
}
