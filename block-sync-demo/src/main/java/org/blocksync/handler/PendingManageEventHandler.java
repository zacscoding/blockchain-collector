package org.blocksync.handler;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.blocksync.entity.Node;
import org.blocksync.factory.PrintStreamFactory;
import org.blocksync.manager.NodeManager;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionResult;
import org.web3j.protocol.core.methods.response.Transaction;

/**
 * Listen pending event & putting into the set.
 * If new block is created, then check pending tx result.
 *
 * TODO :: Check thread safe Putting & Getting in multiple nodes
 *
 * @author zacconding
 * @Date 2018-05-15
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
public class PendingManageEventHandler extends BlockEventHandlerAdapter {

    private LinkedHashSet<String> pendingSets = new LinkedHashSet<>(100, 0.999999f);
    private NodeManager nodeManager;
    private String logDir;

    public PendingManageEventHandler(String logDir, NodeManager nodeManager) {
        Objects.requireNonNull(nodeManager, "Node manage must be not null");
        this.nodeManager = nodeManager;
        this.logDir = logDir;
    }

    @Override
    public void onBlock(Node node, EthBlock ethBlock) {
        Web3j web3j = nodeManager.getWeb3jFromName(node.getName());

        List<TransactionResult> txResults = ethBlock.getBlock().getTransactions();
        if (!txResults.isEmpty()) {
            PrintStream ps = PrintStreamFactory.getPrintStream(logDir, "[PendingResult]", node);

            for (TransactionResult<Transaction> txResult : txResults) {
                if (pendingSets.remove(txResult.get().getHash())) {
                    ps.println("## Pending status : Success(0x1) [" + txResult.get().getHash() + "]");
                }
            }

            checkPendingTxResults(web3j, ps);
        }
    }

    @Override
    public void onPendingTransaction(Node node, Transaction tx) {
        pendingSets.add(tx.getHash());
    }

    private void checkPendingTxResults(Web3j web3j, PrintStream ps) {
        Iterator<String> pendingItr = pendingSets.iterator();
        List<String> removed = new LinkedList<>();

        while (pendingItr.hasNext()) {
            String hash = pendingItr.next();
            try {
                web3j.ethGetTransactionReceipt(hash).send().getTransactionReceipt().ifPresent(tr -> {
                    String resultString = tr.getStatus().equals("0x1") ? "Success(0x1)" : ("Fail(" + tr.getStatus() + ")");
                    ps.println("## Pending status : " + resultString + " [" + tr.getTransactionHash() + "]");
                    removed.add(tr.getTransactionHash());
                });
            } catch (Exception e) {
                // ignore
            }
        }

        for (String removedHash : removed) {
            pendingSets.remove(removedHash);
        }

        ps.println("## Check pending set size : " + pendingSets.size());
    }
}
