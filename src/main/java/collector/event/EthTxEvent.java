package collector.event;

import collector.network.ethereum.EthereumNode;
import lombok.Getter;
import lombok.Setter;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

/**
 * Transaction + Transaction Receipt event
 *
 * @author zacconding
 * @Date 2018-12-20
 * @GitHub : https://github.com/zacscoding
 */
@Getter
@Setter
public class EthTxEvent extends EthEvent {

    private String networkName;
    private EthereumNode ethereumNode;
    private Transaction transaction;
    private TransactionReceipt transactionReceipt;

    public EthTxEvent() {
        super(EthereumEventType.TRANSACTION);
    }

    public EthTxEvent(String networkName, EthereumNode ethereumNode,
        Transaction transaction, TransactionReceipt transactionReceipt) {

        super(EthereumEventType.TRANSACTION);

        this.networkName = networkName;
        this.ethereumNode = ethereumNode;
        this.transaction = transaction;
        this.transactionReceipt = transactionReceipt;
    }

    @Override
    public String toSimpleString() {
        return String.format("network : %s / node : %s / tx : %s block : %s"
            , networkName, ethereumNode.getNodeName(), transaction.getHash(), transaction.getBlockNumber());
    }
}
