package org.blocksync.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.blocksync.entity.Node;
import org.blocksync.factory.PrintStreamFactory;
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

    private String logDir;
    private boolean dumpBlock;
    private boolean dumpPendingTx;

    public DumpEventHandler(String logDir, boolean dumpBlock, boolean dumpPendingTx) {
        this.logDir = logDir;
        this.dumpBlock = dumpBlock;
        this.dumpPendingTx = dumpPendingTx;
    }

    @Override
    public void onBlock(Node node, EthBlock ethBlock) {
        Block block = ethBlock.getBlock();
        log.info("## Receive block [{} - {}]  number : {} , hash : {}, tx count : {}"
            , node.getName(), node.getUrl(), block.getNumber(), block.getHash(), block.getTransactions().size());

        if (dumpBlock) {
            PrintStream ps = PrintStreamFactory.getPrintStream(logDir, "[Block]", node);
            GsonUtil.printGsonPretty(ps, new BlockWrapper(block));
        }
    }

    @Override
    public void onPendingTransaction(Node node, Transaction tx) {
        log.info("## Receive pending tx. hash : {}, from : {}, to : {}, value : {} ", tx.getHash(), tx.getFrom(), tx.getTo(), tx.getValue());

        if (dumpPendingTx) {
            PrintStream ps = PrintStreamFactory.getPrintStream(logDir, "[PendingTx]", node);
            GsonUtil.printGsonPretty(ps, tx);
        }
    }
}