package org.blocksync.handler;

import java.io.PrintStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import org.blocksync.entity.Node;
import org.blocksync.entity.Pair;
import org.blocksync.factory.PrintStreamFactory;
import org.blocksync.manager.ParityNodeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.http.HttpService;

/**
 * Check block hash same
 * between two block chain
 *
 * @author zacconding
 * @Date 2018-05-25
 * @GitHub : https://github.com/zacscoding
 */
public class CheckBlockHashEventHandler extends BlockEventHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(CheckBlockHashEventHandler.class);

    private String logDir;
    private List<Web3j> web3jList;
    private PriorityQueue<Integer> randomQue;
    private Queue<Pair<BigInteger, String>> uncheckedQueue;
    private ParityNodeManager parityNodeManager;

    public CheckBlockHashEventHandler(String logDir, ParityNodeManager parityNodeManager) {
        this.logDir = logDir;
        this.parityNodeManager = parityNodeManager;
        init();
    }

    @Override
    public void onBlock(Node node, EthBlock ethBlock) {
        Block block = ethBlock.getBlock();
        logger.info("## Receive block : {} => {}", block.getNumber(), block.getHash());

        uncheckedQueue.add(new Pair(block.getNumber(), block.getHash()));

        for (int i = 0; i < web3jList.size(); i++) {
            randomQue.offer(i);
        }

        while (!randomQue.isEmpty()) {
            Web3j web3j = web3jList.get(randomQue.poll());
            try {
                while(!uncheckedQueue.isEmpty()) {
                    Pair<BigInteger, String> unchecked = uncheckedQueue.peek();
                    Block findBlock = web3j.ethGetBlockByHash(unchecked.getSecond(), false).send().getBlock();
                    if(findBlock == null) {
                        logger.info("## Checked {}[{}] => not exist", unchecked.getFirst(), unchecked.getSecond());
                    } else {
                        PrintStream ps = PrintStreamFactory.getPrintStream(logDir,"[SameHash]");
                        ps.println(String.format("## Find same hash : %s[%s]", unchecked.getFirst().toString(), unchecked.getSecond()));
                    }
                    uncheckedQueue.poll();
                }
            } catch(Exception e) {
                // ignore
            }
        }
    }

    private void init() {
        List<String> urls = Arrays.asList(
            "http://192.168.79.128:8540"
        );
        web3jList = new ArrayList<>();

        for(String url : urls) {
            web3jList.add(Web3j.build(new HttpService(url)));
        }
        randomQue = new PriorityQueue<>((o1, o2) -> new Random().nextInt() % 2 == 0 ? 1 : -1);
        uncheckedQueue = new LinkedList<>();

        Web3j web3j = parityNodeManager.getWeb3jList().get(0);
        try {
            BigInteger latestBlockNumber = web3j.ethBlockNumber().send().getBlockNumber();
            BigInteger startBlockNumber = BigInteger.ONE;

            while (startBlockNumber.compareTo(latestBlockNumber) < 0) {
                Block block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(startBlockNumber), false).send().getBlock();
                uncheckedQueue.offer(new Pair<>(block.getNumber(), block.getHash()));
                startBlockNumber = startBlockNumber.add(BigInteger.ONE);
            }
        } catch(Exception e) {
            logger.error("## Failed to initialize");
        }
    }
}