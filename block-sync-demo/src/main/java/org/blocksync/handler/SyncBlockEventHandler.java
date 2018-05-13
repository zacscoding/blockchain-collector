package org.blocksync.handler;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import lombok.extern.slf4j.Slf4j;
import org.blocksync.entity.Node;
import org.blocksync.manager.NodeManager;
import org.blocksync.util.SimpleLogger;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.utils.Numeric;

/**
 * @author zacconding
 * @Date 2018-05-12
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
public class SyncBlockEventHandler implements BlockEventHandler {

    // private BlockingQueue<BigInteger> newBlockNumberQueue = new LinkedBlockingQueue<>();
    private PriorityBlockingQueue<BigInteger> newBlockNumberQueue = new PriorityBlockingQueue<>(50);
    private NodeManager nodeManager;
    private Thread syncThread;
    private BigInteger synchronizedBlockNumber;


    public SyncBlockEventHandler(NodeManager nodeManager) {
        this.nodeManager = nodeManager;
        init();
    }

    @Override
    public void onBlock(Node node, EthBlock ethBlock) {
        newBlockNumberQueue.offer(ethBlock.getBlock().getNumber());
    }

    private void init() {
        synchronizedBlockNumber = BigInteger.valueOf(-1);

        syncThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    BigInteger newBlockNumber = newBlockNumberQueue.take();
                    log.info("\n## =============================   Check block : {}({})  ============================= ##", newBlockNumber, Numeric.encodeQuantity(newBlockNumber));
                    Set<BigInteger> pendingBlockNumberSet = new HashSet<>(newBlockNumberQueue.size() + 10, 0.999999F);
                    newBlockNumberQueue.iterator().forEachRemaining(n -> pendingBlockNumberSet.add(n));
                    log.info("## Blocking queue size : {} , distinguish block number size : {}", newBlockNumberQueue.size(), pendingBlockNumberSet.size());

                    if (newBlockNumber.compareTo(synchronizedBlockNumber) <= 0) {
                        log.info("## {} is synchronized already", synchronizedBlockNumber);
                        continue;
                    }

                    // TODO :: Compare data store

                    List<Web3j> web3jList = nodeManager.getWeb3jList();

                    Block block = null;
                    boolean isSynchronized = true;

                    for(int i=0; i<web3jList.size(); i++) {
                        Web3j web3j = web3jList.get(i);
                        Node node = nodeManager.getNode(i);
                        log.info("## Check node : " + node);

                        try {
                            Block received = null;

                            int tryCount = 50;
                            while(tryCount-- > 0) {
                                BigInteger latest = web3j.ethBlockNumber().send().getBlockNumber();

                                if (newBlockNumber.compareTo(latest) <= 0) {
                                    received = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(newBlockNumber), true).send().getBlock();
                                    break;
                                }

                                Thread.sleep(100L);
                            }

                            if(received == null) {
                                log.info("## Failed to get block : {} from {}", newBlockNumber, nodeManager.getNode(i));
                                isSynchronized = false;
                                break;
                            }

                            if (block == null) {
                                block = received;
                                continue;
                            }

                            if (!block.equals(received)) {
                                SimpleLogger.println("## Find different block : " + newBlockNumber);
                                isSynchronized = false;
                                break;
                            }
                        } catch(Exception e) {
                            log.error("Exception occur during getting block", e);
                        }
                    }

                    //SimpleLogger.info("## Result of synchronize block. {}[{}] ==> {} // que size : {}", newBlockNumber, Numeric.encodeQuantity(newBlockNumber), isSynchronized, newBlockNumberQueue.size());
                    if(isSynchronized) {
                        synchronizedBlockNumber = newBlockNumber;
                        log.info("## Synchronized : " + newBlockNumber);
                    } else {
                        newBlockNumberQueue.add(newBlockNumber);
                        log.info("## Not synchronized yet : " + newBlockNumber);
                    }

                    log.info("## ==================================================================================================== ##\n");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        syncThread.setDaemon(true);
        syncThread.start();
    }
}