package org.observer;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

/**
 * @author zacconding
 * @Date 2018-05-03
 * @GitHub : https://github.com/zacscoding
 */
public class ConnectionTest {

    List<String> httpUrls;

    @Before
    public void setUp() {
        httpUrls = Arrays.asList(
            "http://192.168.79.128:8540",
            "http://192.168.79.128:8541",
            "http://192.168.79.128:8542"
        );
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
    }

    @Test
    public void conn() throws Exception {
        for(String url : httpUrls) {
            try {
                Web3j web3j = Web3j.build(new HttpService(url));
                String version = web3j.web3ClientVersion().send().getWeb3ClientVersion();
                System.out.println(String.format("## %s --> %s", url, version));
            } catch(Throwable t) {
                System.out.println(String.format("## %s can`t connect", url));
            }
        }
    }
}
