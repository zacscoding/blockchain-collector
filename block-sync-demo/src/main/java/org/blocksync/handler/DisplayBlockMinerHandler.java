package org.blocksync.handler;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import org.blocksync.entity.Node;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.Block;

/**
 * @author zacconding
 * @Date 2018-05-14
 * @GitHub : https://github.com/zacscoding
 */
public class DisplayBlockMinerHandler implements BlockEventHandler {

    private static final Object lock = new Object();

    private Map<String, String> miners = new HashMap<>();
    private int minerCount = 0;


    @Override
    public void onBlock(Node node, EthBlock ethBlock) {
        Block block = ethBlock.getBlock();
        String miner = block.getMiner();
        String name = miners.get(miner);
        if(name == null) {
            synchronized (lock) {
                if((name = miners.get(miner)) == null) {
                    name = "Miner" + minerCount++;
                    miners.put(miner, name);
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("## =========== Check miner ===========").append("\n")
          .append("## Block number : ").append(block.getNumber()).append("\n")
          .append("## Miner : ").append(name).append("[").append(miner).append("]\n")
          .append("## Timestamp : ").append(block.getTimestamp()).append("\n");

        String log = sb.toString();
        System.out.println(log);
    }
}
