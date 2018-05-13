package org.observer.handler;

import com.fasterxml.jackson.databind.node.BigIntegerNode;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock.Block;

/**
 * @author zacconding
 * @Date 2018-05-03
 * @GitHub : https://github.com/zacscoding
 */
public class DisplayBlockMinersInform implements BlockEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(DisplayPreviousBlocksHandler.class);
    private PrintStream ps;
    private Map<String, Integer> miners;

    public DisplayBlockMinersInform() {
        // ps = System.out;
        try {
            // create file
            File file = new File("D:\\test\\miners.log");
            ps = new PrintStream(new FileOutputStream(file), true);

            // putting miners
            miners = new HashMap<>();
            miners.put("0x00bd138abd70e2f00903268f3db08f2d25677c9e", Integer.valueOf(0));
            miners.put("0x00aa39d30f0d20ff03a22ccfc30b7efbfca597c2", Integer.valueOf(1));
            miners.put("0x002e28950558fbede1a9675cb113f0bd20912019", Integer.valueOf(2));
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleBlock(String url, Block block, Web3j web3j) {
        BigInteger number = block.getNumber();
        StringBuilder sb = new StringBuilder();

        sb.append("### Receive new block : ").append(number).append("   ").append(url).append("  ### \n")
          .append("## Miner : ").append(block.getMiner()).append(" == ").append(miners.get(block.getMiner())).append("").append("\n")
          .append("## Timestamp : ").append(block.getTimestamp()).append("  ").append("timestamp mod d : ").append((block.getTimestamp().mod(BigInteger.valueOf(5)))).append("\n")
          .append("## ============================================================================================================= ##").append("\n");

        String log = sb.toString();
        System.out.println(log);
        ps.println(log);
    }

    @Override
    public void handleError(Throwable t) {
        commonHandleError(logger, t);
    }
}
