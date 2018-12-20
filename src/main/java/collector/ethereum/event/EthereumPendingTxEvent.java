package collector.ethereum.event;

import lombok.Getter;
import lombok.Setter;
import org.web3j.protocol.core.methods.response.Transaction;

/**
 * @author zacconding
 * @Date 2018-12-20
 * @GitHub : https://github.com/zacscoding
 */
@Getter
@Setter
public class EthereumPendingTxEvent extends EthereumEvent {

    private String networkName;
    private String nodeName;
    private Transaction pendingTx;

    public EthereumPendingTxEvent(String networkName, String nodeName, Transaction pendingTx) {
        super(EthereumEventType.PENDING_TX);

        this.networkName = networkName;
        this.nodeName = nodeName;
        this.pendingTx = pendingTx;
    }
}
