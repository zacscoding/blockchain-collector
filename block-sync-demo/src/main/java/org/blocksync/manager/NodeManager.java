package org.blocksync.manager;

import java.util.List;
import org.blocksync.entity.Node;
import org.web3j.protocol.Web3j;

/**
 * @author zacconding
 * @Date 2018-05-12
 * @GitHub : https://github.com/zacscoding
 */
public interface NodeManager {

    List<Node> getNodes();

    Node getNode(int idx);

    Node getNodeFromName(String name);

    Node getNodeFromUrl(String url);

    List<Web3j> getWeb3jList();

    Web3j getWeb3j(int idx);

    Web3j getWeb3jFromName(String name);

    Web3j getWeb3jFromUrl(String url);
}