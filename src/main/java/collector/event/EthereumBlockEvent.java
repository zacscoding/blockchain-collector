package collector.event;

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
    private String nodeName;
    private Block block;

    public EthereumBlockEvent(String networkName, String nodeName, Block block) {
        super(EthereumEventType.BLOCK);
        this.networkName = networkName;
        this.nodeName = nodeName;
        this.block = block;
    }
}
