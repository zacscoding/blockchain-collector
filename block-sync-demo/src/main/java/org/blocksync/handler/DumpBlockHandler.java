package org.blocksync.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.blocksync.entity.Node;
import org.blocksync.util.GsonUtil;
import org.blocksync.util.SimpleLogger;
import org.blocksync.wrapper.BlockWrapper;
import org.springframework.util.StringUtils;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.Block;

/**
 * @author zacconding
 * @Date 2018-05-12
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
public class DumpBlockHandler implements BlockEventHandler {

    private static Object lock = new Object();

    private Map<String, PrintStream> printStreamMap;
    private String logDir;
    private boolean dump;

    public DumpBlockHandler(String logDir) {
        this.logDir = logDir;
        if (StringUtils.hasText(logDir)) {
            dump = true;
            printStreamMap = new HashMap<>();
        } else {
            log.info("## Not exist observe dump path");
        }
    }

    @Override
    public void onBlock(Node node, EthBlock ethBlock) {
        Block block = ethBlock.getBlock();
        SimpleLogger.println("## Receive block [{} - {}]  number : {} , hash : {}, tx count : {}", node.getName(), node.getUrl(), block.getNumber(), block.getHash(), block.getTransactions().size());

        if (dump) {
            PrintStream ps = printStreamMap.get(node);

            if (ps == null) {
                synchronized (lock) {
                    if ((ps = printStreamMap.get(node.getName())) == null) {
                        ps = createPrintStream(node);
                        printStreamMap.put(node.getName(), ps);
                    }
                }
            }

            GsonUtil.printGsonPretty(ps, new BlockWrapper(block));
        }
    }

    private PrintStream createPrintStream(Node node) {
        try {
            // create log file
            String nodeName = node.getName() + "-" + node.getUrl().substring("http://".length()).replace(':', '-');
            File file = new File(logDir, nodeName + ".log");
            return new PrintStream(new FileOutputStream(file), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}