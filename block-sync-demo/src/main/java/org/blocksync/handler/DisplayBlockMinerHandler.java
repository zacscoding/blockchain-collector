package org.blocksync.handler;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import org.blocksync.entity.Node;
import org.blocksync.factory.PrintStreamFactory;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.Block;

/**
 * If new block is created, display block`s miner.
 * It is useful if u use POA.
 *
 * @author zacconding
 * @Date 2018-05-14
 * @GitHub : https://github.com/zacscoding
 */
public class DisplayBlockMinerHandler extends BlockEventHandlerAdapter {

    private static final Object lock = new Object();

    private Map<String, String> miners = new HashMap<>();
    private int minerCount = 0;
    private String logDir;

    public DisplayBlockMinerHandler(String logDir) {
        this.logDir = logDir;
    }

    @Override
    public void onBlock(Node node, EthBlock ethBlock) {
        Block block = ethBlock.getBlock();
        String miner = block.getMiner();
        String name = miners.get(miner);
        if (name == null) {
            synchronized (lock) {
                if ((name = miners.get(miner)) == null) {
                    name = "Miner" + minerCount++;
                    miners.put(miner, name);
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("## =========== Check miner ===========").append("\n").append("## Block number : ").append(block.getNumber()).append("\n")
          .append("## Miner : ").append(name).append("[").append(miner).append("]\n").append("## Timestamp : ").append(block.getTimestamp())
          .append("\n");

        PrintStream ps = PrintStreamFactory.getPrintStream(logDir, "[MINER-CHECK]", node);
        ps.println(sb.toString());
    }
}
