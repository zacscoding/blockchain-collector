package org.blocksync.service;

import java.io.PrintStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.annotation.PostConstruct;
import org.blocksync.entity.Node;
import org.blocksync.entity.Pair;
import org.blocksync.factory.PrintStreamFactory;
import org.blocksync.manager.NodeManager;
import org.blocksync.manager.ParityNodeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.http.HttpService;

/**
 * @author zacconding
 * @Date 2018-05-18
 * @GitHub : https://github.com/zacscoding
 */
@Service
public class SendTxService {

    private List<Node> nodes;
    private List<Pair<String, String>> addrs;
    private Thread sendTxThread;
    private PrintStream ps;

    @Autowired
    private ParityNodeManager parityNodeManager;
    @Value("${observe.log.path}")
    private String blockLogDir;

    @Value("${task.send.tx}")
    private boolean sendTask;

    @PostConstruct
    private void setUp() {
        if(sendTask) {
            nodes = parityNodeManager.getNodes();
            addrs = Arrays.asList(
                new Pair<>("0x000e1f8e17224e00e7179a3235d431684758250e", "zac1"),
                new Pair<>("0x00e5dc96b57f4f185296346b03b6d8d70ec56b5d", "zac2"),
                new Pair<>("0x00ef1bdc7b0392b4fa529c1f0f0902a3ac3b1a73", "zac3"),
                new Pair<>("0x00e0fcd57c93639ca311217bc107a93b3c4dc4a9", "zac4")
            );

            ps = PrintStreamFactory.getPrintStream(blockLogDir, "[Send-Tx]results");
            sendTxThread = new Thread(createSendTask());
            sendTxThread.setDaemon(true);
            sendTxThread.start();
        }
    }

    private Runnable createSendTask() {
        return () -> {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    Pair<Integer, Integer> indices = getDiffIdx();
                    Node node = nodes.get(new Random().nextInt(nodes.size()));
                    Admin web3j = Admin.build(new HttpService(node.getUrl()));
                    BigInteger value = BigInteger.valueOf(new Random().nextInt(1000000));
                    String from = addrs.get(indices.getFirst()).getFirst();
                    String to = addrs.get(indices.getSecond()).getFirst();
                    Transaction tx = new Transaction(
                        from,
                        null,
                        null,
                        null,
                        to,
                        value,
                        null
                    );
                    String hash = web3j.personalSendTransaction(tx,addrs.get(indices.getFirst()).getSecond()).send().getTransactionHash();
                    ps.println("#Send tx from : " + from + ", to : " + to + ", value : " + value + " ==> hash : " + hash);
                    Thread.sleep(new Random().nextInt(10000));
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private Pair<Integer, Integer> getDiffIdx() {
        int addrCount = addrs.size();
        int fromIdx = new Random().nextInt(addrCount);
        int toIdx = -1;
        do {
            toIdx = new Random().nextInt(addrCount);
        } while (toIdx != -1 && toIdx == fromIdx);

        return new Pair<>(fromIdx, toIdx);
    }
}
