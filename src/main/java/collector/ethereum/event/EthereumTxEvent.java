package collector.ethereum.event;

import collector.ethereum.EthereumNode;
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
public class EthereumTxEvent extends EthereumEvent {

    private String networkName;
    private EthereumNode ethereumNode;
    private Transaction transaction;
    private TransactionReceipt transactionReceipt;

    public EthereumTxEvent() {
        super(EthereumEventType.TRANSACTION);
    }

    public EthereumTxEvent(String networkName, EthereumNode ethereumNode,
        Transaction transaction, TransactionReceipt transactionReceipt) {

        super(EthereumEventType.TRANSACTION);

        this.networkName = networkName;
        this.ethereumNode = ethereumNode;
        this.transaction = transaction;
        this.transactionReceipt = transactionReceipt;
    }
}
