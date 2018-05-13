package org.observer.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import org.observer.util.GsonUtil;
import org.observer.wrapper.BlockWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock.Block;

/**
 * Display previous block
 *
 * @author zacconding
 * @Date 2018-05-03
 * @GitHub : https://github.com/zacscoding
 */
public class DisplayPreviousBlocksHandler implements BlockEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(DisplayPreviousBlocksHandler.class);

    private String logDir;
    private Map<String, PrintStream> printStreamMap;

    public DisplayPreviousBlocksHandler() {
        logDir = System.getProperty("observe.log.path");
        printStreamMap = new ConcurrentHashMap<>();
    }

    @Override
    public void handleBlock(String url, Block block, Web3j web3j) {
        PrintStream ps = printStreamMap.get(url);
        if (ps == null) {
            ps = createPrintStream(url);
            printStreamMap.put(url, ps);
        }

        PriorityQueue<BlockWrapper> que = new PriorityQueue<>(10, (BlockWrapper b1, BlockWrapper b2) -> b1.getNumber().subtract(b2.getNumber()).intValue());
        ps.println("## Receive new block : " + block.getNumber());
        logger.info("## Receive new block [{}] : {}", url, block.getNumber());
        try {
            que.add(new BlockWrapper(block));
            for(int i=1; i<30; i++) {
                BigInteger num = block.getNumber().subtract(BigInteger.valueOf(i));
                if(num.compareTo(BigInteger.valueOf(0)) < 0) {
                    break;
                }
                que.add(new BlockWrapper(web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(num), true).send().getBlock()));
            }
            GsonUtil.printGsonPretty(ps,que);
            ps.println("========================================================================================================================================================================");
        } catch(IOException e) {
            logger.error("Failed to send ethGetBlockByNumber : " + url, e);
        }
    }

    @Override
    public void handleError(Throwable t) {
        commonHandleError(logger, t);
    }

    private PrintStream createPrintStream(String url) {
        try {
            // create log file
            String nodeName = "Node-" + url.substring("http://".length()).replace(':', '-');
            File file = new File(logDir, nodeName + ".log");
            PrintStream ps = new PrintStream(new FileOutputStream(file), true);
            ps.println("## Start to observe : " + new SimpleDateFormat("yyMMdd-HH:mm:ss").format(System.currentTimeMillis()));
            return ps;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
