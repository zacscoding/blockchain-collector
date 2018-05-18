package org.blocksync;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.blocksync.entity.Node;
import org.blocksync.entity.Pair;
import org.junit.Before;
import org.junit.Test;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.http.HttpService;

/**
 * @author zacconding
 * @Date 2018-05-18
 * @GitHub : https://github.com/zacscoding
 */
public class SendTxTest {

    List<Pair<String, String>> addrs;
    List<Node> nodes;

    @Before
    public void setUp() {
        addrs = Arrays.asList(
            new Pair<>("0x000e1f8e17224e00e7179a3235d431684758250e", "zac1"),
            new Pair<>("0x00e5dc96b57f4f185296346b03b6d8d70ec56b5d", "zac2"),
            new Pair<>("0x00ef1bdc7b0392b4fa529c1f0f0902a3ac3b1a73", "zac3"),
            new Pair<>("0x00e0fcd57c93639ca311217bc107a93b3c4dc4a9", "zac4")
        );

        nodes = Arrays.asList(
            new Node("node2", "http://192.168.79.128:8540"),
            new Node("node3", "http://192.168.79.129:8540"),
            new Node("node4", "http://192.168.79.130:8540"),
            new Node("node5", "http://192.168.79.131:8540")
        );
    }

    @Test
    public void sendTaskTest() throws Exception {
        Thread t = new Thread(createSendTask());
        t.setDaemon(true);
        t.start();

        Thread.sleep(5000L);
    }

    private Runnable createSendTask() {
        return () -> {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    Pair<Integer, Integer> indices = getDiffIdx();
                    Node node = nodes.get(new Random().nextInt(nodes.size()));
                    Admin web3j = Admin.build(new HttpService(node.getUrl()));
                    Transaction tx = new Transaction(
                        addrs.get(indices.getFirst()).getFirst(),
                        null,
                        null,
                        null,
                        addrs.get(indices.getSecond()).getFirst(),
                        BigInteger.valueOf(new Random().nextInt(1000000)),
                        null
                    );
                    String hash = web3j.personalSendTransaction(tx,addrs.get(indices.getFirst()).getSecond()).send().getTransactionHash();
                    System.out.println(hash);
                    Thread.sleep(new Random().nextInt(100));
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

    @Test
    public void temp() {

    }

    @Test
    public void sendTest() throws Exception {
        Admin web3j = Admin.build(new HttpService(nodes.get(0).getUrl()));
        Transaction tx = new Transaction(
            "0x000e1f8e17224e00e7179a3235d431684758250e",
            null,
            null,
            null,
            "0x00e5dc96b57f4f185296346b03b6d8d70ec56b5d",
            BigInteger.valueOf(1),
            null
        );

        String hash = web3j.personalSendTransaction(tx,"zac1").send().getTransactionHash();
        System.out.println(hash);
    }
}


