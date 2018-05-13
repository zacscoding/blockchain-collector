package org.blocksync;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import org.blocksync.entity.Node;
import org.junit.Test;

/**
 * @author zacconding
 * @Date 2018-05-12
 * @GitHub : https://github.com/zacscoding
 */
public class SplitNodeTest {

    @Test
    public void splitTest() {
        List<Node> nodes = extractNode(null);
        assertTrue(nodes.size() == 0);

        nodes = extractNode("");
        assertTrue(nodes.size() == 0);

        nodes = extractNode("node0:http://192.168.79.128:8540,node1:http://192.168.79.128:8541,node2:http://192.168.79.128:8542");
        assertTrue(nodes.size() == 3);
        Node node0 = nodes.get(0);
        assertThat(node0.getName(), is("node0"));
        assertThat(node0.getUrl(), is("http://192.168.79.128:8540"));

        Node node1 = nodes.get(1);
        assertThat(node1.getName(), is("node1"));
        assertThat(node1.getUrl(), is("http://192.168.79.128:8541"));

        Node node2 = nodes.get(2);
        assertThat(node2.getName(), is("node2"));
        assertThat(node2.getUrl(), is("http://192.168.79.128:8542"));

        try {
            extractNode("node1:");
            fail();
        } catch(Exception e) {
            System.out.println("Message : " + e.getMessage());
        }
    }


    public List<Node> extractNode(String config) {
        if(config == null || config.length() == 0) {
            return Collections.emptyList();
        }

        StringTokenizer st = new StringTokenizer(config, ",");
        int count = st.countTokens();

        if (count < 1) {
            return Collections.emptyList();
        }

        List<Node> nodes = new ArrayList<>(count);

        while (st.hasMoreElements()) {
            String value = st.nextToken();
            int urlIdx = value.indexOf(":http");

            if (urlIdx < 0 || urlIdx == value.length() - 1) {
                throw new RuntimeException("Invalid Node Config [" + value + "] Use nodeName:url e.g) node1:http://ip:port");
            }

            String nodeName = value.substring(0, urlIdx);
            String url = value.substring(urlIdx + 1);

            nodes.add(new Node(nodeName, url));
        }

        return nodes;
    }


}
