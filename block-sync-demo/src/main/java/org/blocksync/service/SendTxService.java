package org.blocksync.service;

import java.io.PrintStream;
import java.math.BigInteger;
import java.util.ArrayList;
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

    private Random random = new Random();
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
    @Value("${task.send.tx.sleep.bound}")
    private int sleepBound;
    @Value("${task.send.tx.nodename}'.split(',')}")
    private List<String> nodeNames;


    @PostConstruct
    private void setUp() {
        if(sendTask) {
            initNodes();
            addrs = Arrays.asList(
                new Pair<>("0x00d695cd9b0ff4edc8ce55b493aec495b597e235", "user1"),
                new Pair<>("0x001ca0bb54fcc1d736ccd820f14316dedaafd772", "user2"),
                new Pair<>("0x00cb25f6fd16a52e24edd2c8fd62071dc29a035c", "user3"),
                new Pair<>("0x0046f91449e4b696d48c9dd10703cb589649c265", "user4"),
                new Pair<>("0x00cc5a03e7166baa2df1d449430581d92abb0a1e", "user5"),
                new Pair<>("0x0095e961b3a00f882326bbc8f0a469e5b56e858a", "user6"),
                new Pair<>("0x0008fba8d298de8f6ea7385d447f4d3252dc0880", "user7"),
                new Pair<>("0x0094bc2c3b585928dfeaf85e96ba57773c0673c1", "user8"),
                new Pair<>("0x0002851146112cef5d360033758c470689b72ea7", "user9"),
                new Pair<>("0x002227d6a35ed31076546159061bd5d3fefe9f0a", "user10")
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

                    for(String nodeName : nodeNames) {
                        Node node = parityNodeManager.getNodeFromName(nodeName);

                        if(node == null) {
                            continue;
                        }

                        Admin web3j = Admin.build(new HttpService(node.getUrl()));
                        String hash = web3j.personalSendTransaction(tx,addrs.get(indices.getFirst()).getSecond()).send().getTransactionHash();
                        ps.println("#Send tx from : " + from + ", to : " + to + ", value : " + value + " ==> hash : " + hash);
                    }
                    Thread.sleep(random.nextInt(sleepBound));
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

    private void initNodes() {
        nodes = new ArrayList<>();
        for (String nodeName : nodeNames) {
            Node node = parityNodeManager.getNodeFromName(nodeName);
            if (node != null) {
                nodes.add(node);
            }
        }
    }
}
