package org.blocksync.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.blocksync.entity.Node;
import org.blocksync.funtional.UnderScore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

/**
 * @author zacconding
 * @Date 2018-05-12
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
@Component("parityNodeManager")
public class ParityNodeManager implements NodeManager {

    private List<Node> nodes;
    private List<Web3j> web3jList;

    @Value("${parity.nodes:}")
    private String nodeValues;

    @PostConstruct
    private void setUp() {
        init();
    }

    @Override
    public List<Node> getNodes() {
        return nodes;
    }

    @Override
    public Node getNode(int idx) {
        if (!isRange(idx)) {
            return null;
        }

        return nodes.get(idx);
    }

    @Override
    public Node getNodeFromName(String name) {
        return UnderScore.find(nodes, node -> node.getName().equals(name));
    }

    @Override
    public Node getNodeFromUrl(String url) {
        return UnderScore.find(nodes, node -> node.getUrl().equals(url));
    }

    @Override
    public List<Web3j> getWeb3jList() {
        return web3jList;
    }

    @Override
    public Web3j getWeb3j(int idx) {
        if (isRange(idx)) {
            return web3jList.get(idx);
        }

        return null;
    }

    @Override
    public Web3j getWeb3jFromName(String name) {
        int idx = UnderScore.findIndex(nodes, node -> node.getName().equals(name));
        return getWeb3j(idx);
    }

    @Override
    public Web3j getWeb3jFromUrl(String url) {
        int idx = UnderScore.findIndex(nodes, node -> node.getName().equals(url));
        return getWeb3j(idx);
    }

    private void init() {
        nodes = extractNode(nodeValues);

        if (nodes.isEmpty()) {
            log.error("Nodes must not empty");
            System.exit(-1);
        }

        log.info("## Initialize nodes.");
        web3jList = new ArrayList<>(nodes.size());

        for (Node node : nodes) {
            log.info("## Extract node {}-{}", node.getName(), node.getUrl());
            Web3j web3j = Web3j.build(new HttpService(node.getUrl()));
            web3jList.add(web3j);
        }
    }

    private boolean isRange(int idx) {
        return idx >= 0 && idx < nodes.size();
    }

    private List<Node> extractNode(String config) {
        if (config == null || config.length() == 0) {
            return Collections.emptyList();
        }

        StringTokenizer st = new StringTokenizer(config, ",");
        int count = st.countTokens();

        if (count < 1) {
            return Collections.emptyList();
        }

        List<Node> ret = new ArrayList<>(count);

        while (st.hasMoreElements()) {
            String value = st.nextToken();
            int urlIdx = value.indexOf(":http");

            if (urlIdx < 0 || urlIdx == value.length() - 1) {
                throw new RuntimeException("Invalid Node Config [" + value + "] Use nodeName:url e.g) node1:http://ip:port");
            }

            String nodeName = value.substring(0, urlIdx);
            String url = value.substring(urlIdx + 1);

            int idx = UnderScore.findIndex(ret, node -> node.getName().equals(nodeName) || node.getUrl().equals(url));
            if (idx != -1) {
                log.error("Node name & url must be different. => " + nodeName + ", " + url);
                System.exit(-1);
            }

            ret.add(new Node(nodeName, url));
        }

        return ret;
    }
}