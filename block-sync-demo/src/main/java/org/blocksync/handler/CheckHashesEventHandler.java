package org.blocksync.handler;

import java.io.PrintStream;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.blocksync.entity.Node;
import org.blocksync.factory.PrintStreamFactory;
import org.blocksync.manager.ParityNodeManager;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.http.HttpService;

/**
 * @author zacconding
 * @Date 2018-05-30
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
public class CheckHashesEventHandler extends BlockEventHandlerAdapter {

    private String logDir;
    private ParityNodeManager parityNodeManager;

    public CheckHashesEventHandler(String logDir, ParityNodeManager parityNodeManager) {
        this.logDir = logDir;
        this.parityNodeManager = parityNodeManager;
    }

    @Override
    public void onBlock(Node node, EthBlock ethBlock) {
        log.info("## Receive : {} from {}", ethBlock.getBlock().getNumber(), node);
        Block block = ethBlock.getBlock();
        checkExistHash(true, node, block.getHash());
        block.getTransactions().forEach(tr -> checkExistHash(false, node, ((Transaction)tr).getHash()));
    }

    private void checkExistHash(boolean isBlock, Node node, String hash) {
        List<Node> nodes = parityNodeManager.getNodes();

        for (int i = 0; i < nodes.size(); i++) {
            Node now = nodes.get(i);

            if (now.getName().equals(node.getName())) {
                continue;
            }

            Web3j web3j = parityNodeManager.getWeb3j(i);
            try {
                Object result = null;
                if(isBlock) {
                    result = web3j.ethGetBlockByHash(hash, false).send().getBlock();
                } else {
                    result = web3j.ethGetTransactionByHash(hash).send().getResult();
                }

                if(result != null) {
                    PrintStream ps = PrintStreamFactory.getPrintStream(logDir, "[Check-Hashes-" + node.getName() + "]");
                    StringBuilder sb = new StringBuilder();

                    sb.append("## Find diff hash.\n")
                      .append("Node : ").append(node.toString()).append(", Node : ").append(now.toString()).append("\n")
                      .append("Hash : ").append(hash).append("\n");

                    ps.println(sb.toString());
                } else {
                    System.out.println("## Result is null. hash : " + hash + ", isBlock : " + isBlock);
                }
            } catch(Exception e) {
                // ignore
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Web3j web3j = Web3j.build(new HttpService("http://192.168.5.50:8540/"));
        // Block block = web3j.ethGetBlockByHash("0x523ca958acc410d6b2c1a86065268879e5a6b195965cd4054cfd42684c06ea4a", true).send().getBlock();
        Block block = web3j.ethGetBlockByHash("0xdd871200820b2126500ac36a6f41879721ac879a66c5c4e21a69b345d4766c25", true).send().getBlock();
        System.out.println("Check block : " + block);
        block.getTransactions().forEach(tr -> {
            Transaction tx = (Transaction)tr;
            System.out.println(tx.getHash());
        });
    }
}
