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
import org.web3j.protocol.core.methods.response.Transaction;

/**
 * @author zacconding
 * @Date 2018-05-12
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
public class DumpEventHandler extends BlockEventHandlerAdapter {

    private static Object lock = new Object();

    private Map<String, PrintStream> blockPrintStreamMap;
    private Map<String, PrintStream> pendingTxStreamMap;
    private String logDir;
    private boolean dumpBlock;
    private boolean dumpPendingTx;

    public DumpEventHandler(String logDir, boolean dumpBlock, boolean dumpPendingTx) {
        this.logDir = logDir;
        this.dumpBlock = dumpBlock;
        this.dumpPendingTx = dumpPendingTx;

        if (dumpBlock || dumpPendingTx) {
            if(!StringUtils.hasText(logDir)) {
                logDir = "logs/";
            }

            if(dumpBlock) {
                log.info("## Dump block event");
                blockPrintStreamMap = new HashMap<>();
            }

            if(dumpPendingTx) {
                log.info("## Dump pending transaction event");
                pendingTxStreamMap = new HashMap<>();
            }
        } else {
            log.info("## Not exist observe dump path");
        }
    }

    @Override
    public void onBlock(Node node, EthBlock ethBlock) {
        Block block = ethBlock.getBlock();
        log.info("## Receive block [{} - {}]  number : {} , hash : {}, tx count : {}", node.getName(), node.getUrl(), block.getNumber(), block.getHash(), block.getTransactions().size());

        if (dumpBlock) {
            PrintStream ps = blockPrintStreamMap.get(node);

            if (ps == null) {
                synchronized (lock) {
                    if ((ps = blockPrintStreamMap.get(node.getName())) == null) {
                        ps = createPrintStream("[Block]", node);
                        blockPrintStreamMap.put(node.getName(), ps);
                    }
                }
            }

            GsonUtil.printGsonPretty(ps, new BlockWrapper(block));
        }
    }

    @Override
    public void onPendingTransaction(Node node, Transaction tx) {
        log.info("## Receive pending tx. hash : {}, from : {}, to : {}, value : {} ", tx.getHash(), tx.getFrom(), tx.getTo(), tx.getValue());

        if (dumpPendingTx) {
            PrintStream ps = pendingTxStreamMap.get(node);

            if (ps == null) {
                synchronized (lock) {
                    if ((ps = pendingTxStreamMap.get(node.getName())) == null) {
                        ps = createPrintStream("[PendingTx]", node);
                        pendingTxStreamMap.put(node.getName(), ps);
                    }
                }
            }

            GsonUtil.printGsonPretty(ps, tx);
        }
    }

    private PrintStream createPrintStream(String prefix, Node node) {
        try {
            // create log file
            String nodeName = prefix +  node.getName() + "-" + node.getUrl().substring("http://".length()).replace(':', '-');
            File file = new File(logDir, nodeName + ".log");
            return new PrintStream(new FileOutputStream(file), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}