package org.blocksync;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.blocksync.entity.Node;
import org.junit.Before;
import org.junit.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

/**
 * @author zacconding
 * @Date 2018-05-18
 * @GitHub : https://github.com/zacscoding
 */
public class ConnectionTest {
    List<Node> nodes;

    // 3220171
    @Before
    public void setUp() {
        nodes = Arrays.asList(
            new Node("node2", "http://192.168.7.128:8542/"),
            new Node("node3", "http://192.168.7.128:8543/"),
            new Node("node4", "http://192.168.7.128:8544/")
        );
    }

    @Test
    public void connTest() {
        for (Node node : nodes) {
            Web3j web3j = Web3j.build(new HttpService(node.getUrl()));
            try {
                String version = web3j.web3ClientVersion().send().getWeb3ClientVersion();
                System.out.println("## Success to connect Node : " + node + ", version : " + version);
            } catch(IOException e) {
                System.out.println("## Failed to connect Node : " + node + ", message : " + e.getMessage());
            }
        }
    }
}
