package org.blocksync;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.blocksync.entity.Node;
import org.blocksync.entity.Pair;
import org.blocksync.util.SimpleLogger;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionResult;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
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
            new Pair<>("0x00d695cd9b0ff4edc8ce55b493aec495b597e235", "user1"),
            new Pair<>("0x001ca0bb54fcc1d736ccd820f14316dedaafd772", "user2"),
            new Pair<>("0x00cb25f6fd16a52e24edd2c8fd62071dc29a035c", "user3"),
            new Pair<>("0x0046f91449e4b696d48c9dd10703cb589649c265", "user4")
        );

        nodes = Arrays.asList(
            new Node("node2", "http://192.168.5.50:8540")
        );
    }

    @Test
    public void sendTaskTest() throws Exception {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        Thread t = new Thread(createSendTask());
        t.setDaemon(true);
        t.start();
        TimeUnit.MINUTES.sleep(2L);
        //TimeUnit.SECONDS.sleep(10);*/

        Web3j web3j = Web3j.build(new HttpService(nodes.get(0).getUrl()));
        Block best = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, true).send().getBlock();
        System.out.println("## Best block : " + (best == null ? "null" : best.getNumber()));

        if(best.getNumber().compareTo(BigInteger.ZERO) < 0) {
            return;
        }

        BigInteger start = BigInteger.ZERO;
        // BigInteger last = new BigInteger("26");
        BigInteger last = best.getNumber();

        while (start.compareTo(last) < 0) {
            Block block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(start), true).send().getBlock();
            BigInteger gasLimit = block.getGasLimit();
            BigInteger gasUsed = BigInteger.ZERO;
            BigInteger gasUsed2 = BigInteger.ZERO;

            for(TransactionResult<?> tr : block.getTransactions()) {
                org.web3j.protocol.core.methods.response.Transaction tx = (org.web3j.protocol.core.methods.response.Transaction) tr.get();
                gasUsed = gasUsed.add(tx.getGas());
            }

            int txSize = block.getTransactions().size();
            if (txSize > 0) {
                org.web3j.protocol.core.methods.response.Transaction tx = (org.web3j.protocol.core.methods.response.Transaction) block.getTransactions().get(txSize -1).get();
                TransactionReceipt receipt = web3j.ethGetTransactionReceipt(tx.getHash()).send().getTransactionReceipt().get();
                gasUsed2 = receipt.getCumulativeGasUsed();
            }

            SimpleLogger.println("## Block Num : {}, block.gasLimit : {}, block.gasUsed : {}, tx`s gasUsed : {}, tr`s gas : {},  txCount : {}, gasLimit.compareTo(gasUsed) : {}",
                block.getNumber(), gasLimit, block.getGasUsed(), gasUsed, gasUsed2, block.getTransactions().size(), gasLimit.compareTo(gasUsed));

            start = start.add(BigInteger.ONE);
        }
    }

    private Runnable createSendTask() {
        return () -> {
            BigInteger nonce = BigInteger.ZERO;
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    // Pair<Integer, Integer> indices = getDiffIdx();
                    Pair<Integer, Integer> indices = new Pair<>(0, 1);
                    // Node node = nodes.get(new Random().nextInt(nodes.size()));
                    Node node = nodes.get(0);
                    Admin web3j = Admin.build(new HttpService(node.getUrl()));
                    Transaction tx = new Transaction(
                        addrs.get(indices.getFirst()).getFirst(),
                        nonce,
                        BigInteger.ZERO,
                        null,
                        addrs.get(indices.getSecond()).getFirst(),
                        new BigInteger("10000"),
                        null
                    );
                    String hash = web3j.personalSendTransaction(tx,addrs.get(indices.getFirst()).getSecond()).send().getTransactionHash();
                    if(hash == null) {
                        System.out.println("hash is null");
                    } else {
                        nonce = nonce.add(BigInteger.ONE);
                    }
                    Thread.sleep(5L);

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



