package org.blocksync;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.blocksync.entity.Node;
import org.blocksync.util.GsonUtil;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.http.HttpService;
/**
 * @author zacconding
 * @Date 2018-05-13
 * @GitHub : https://github.com/zacscoding
 */
public class Web3jTest {
    List<Node> nodes;

    @Test
    public void test2() {
        BigInteger value = BigInteger.valueOf(new Random().nextInt(1000000));
        System.out.println(value.toString(16));
    }

    // 3220171
    @Before
    public void setUp() {
        nodes = Arrays.asList(
          new Node("node0", "http://192.168.79.128:8540"),
          new Node("node1", "http://192.168.79.128:8541"),
          new Node("node2", "http://192.168.79.128:8542"),
          new Node("node3", "http://192.168.79.128:8543")
        );
    }

    @Test
    public void getBlockByNumber() throws Exception {
        for(Node node : nodes) {
            Web3j web3j = Web3j.build(new HttpService(node.getUrl()));
            Block block = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, true) .send().getBlock();

            PrintStream ps = createPrintStream(node.getUrl());

            ps.println("## Receive new block : " + block.getNumber());
            try {
                for(int i=1; i<30; i++) {
                    BigInteger num = block.getNumber().subtract(BigInteger.valueOf(i));
                    if(num.compareTo(BigInteger.valueOf(0)) < 0) {
                        break;
                    }

                    Block b = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(num), true).send().getBlock();
                    GsonUtil.printGsonPretty(ps,b);
                }
                ps.println("========================================================================================================================================================================");
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private PrintStream createPrintStream(String url) {
        try {
            // create log file
            String nodeName = "Node-" + url.substring("http://".length()).replace(':', '-');
            File file = new File("D:\\block-observer", nodeName + ".log");
            PrintStream ps = new PrintStream(new FileOutputStream(file), true);
            ps.println("## Start to observe : " + new SimpleDateFormat("yyMMdd-HH:mm:ss").format(System.currentTimeMillis()));
            return ps;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void findHash() throws Exception {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        List<String> hashes = Arrays.asList(
            "0x9a8fa3bdd1b08ca0bfa81c245134516409b62bd6d79f4a05476bba458fe10df9",
          "0x6b1a39c39813b49f012b0cf6cef9b375de2f115b19c55c155cbd322aa65610ea",
          "0x8f8776db24f10a4e97c8284ad72f832bf895f71a7c979ca1c81308ca69ffabdd",
          "0x962dc64feb35b0e378f5ed687155bb0f09782e6e57d5728bc2e904917afc3982",
          "0xf693b7c5bad5b4c00c7b8e8197820bbe673710a2cdc247c24a31a2ab4336532c"
        );

        for(Node node : nodes) {
            Web3j web3j = Web3j.build(new HttpService(node.getUrl()));
            System.out.println("## =========================================================");
            System.out.println("## Check : " + node);

            for(String hash : hashes) {
                System.out.println("## Check : " + hash);
                Optional<org.web3j.protocol.core.methods.response.Transaction> optional =  web3j.ethGetTransactionByHash(hash).send().getTransaction();

                optional.ifPresent(t -> {
                    System.out.println(hash + " is exist");
                });
            }

            System.out.println("## ========================================================= ##");
        }
    }

    @Test
    public void test() throws Exception {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        Web3j web3j = Web3j.build(new HttpService("http://192.168.79.128:8540"));
        List<String> accounts = web3j.ethAccounts().send().getAccounts();
        for(String account : accounts) {
            BigInteger bal = web3j.ethGetBalance(account, DefaultBlockParameterName.LATEST).send().getBalance();
            System.out.println(account + " ==> " + bal);
        }
    }
}
