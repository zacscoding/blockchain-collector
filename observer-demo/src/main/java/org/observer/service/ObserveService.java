package org.observer.service;

import com.fasterxml.jackson.databind.node.BigIntegerNode;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.observer.configuration.Web3jConfiguration;
import org.observer.util.GsonUtil;
import org.observer.wrapper.BlockWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.http.HttpService;
import rx.Subscription;

/**
 * @author zacconding
 * @Date 2018-05-01
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
@Service
public class ObserveService {

    @Autowired
    private Web3jConfiguration web3jConfiguration;
    @Value("${observe.log.path}")
    private String logDir;
    private List<String> urls;
    private Thread[] observer;

    @PostConstruct
    public void startService() {
        urls = web3jConfiguration.getHttpUrls();
        observer = new Thread[urls.size()];
        for (int i = 0; i < observer.length; i++) {
            String url = urls.get(i);
            Thread t = new Thread(createTask(url));
            t.setName("Observer[" + url + "]");
            t.setDaemon(true);
            observer[i] = t;
            log.info("## Start observe service : " + t.getName());
            t.start();
        }
    }

    private Runnable createTask(final String url) {
        return () -> {
            Subscription subscription = null;
            try {
                final Web3j web3j = Web3j.build(new HttpService(url));
                // create log file
                String nodeName = "Node-" + url.substring("http://".length()).replace(':', '-');
                File file = new File(logDir, nodeName + ".log");
                PrintStream ps = new PrintStream(new FileOutputStream(file), true);
                ps.println("## Start to observe : " + new SimpleDateFormat("yyMMdd-HH:mm:ss").format(System.currentTimeMillis()));

                // subscribe block
                subscription = web3j.blockObservable(true).subscribe(
                    (onNext -> {
                        Block block = onNext.getBlock();
                        PriorityQueue<BlockWrapper> que = new PriorityQueue<>(10, (BlockWrapper b1, BlockWrapper b2) -> b1.getNumber().subtract(b2.getNumber()).intValue());
                        ps.println("## Receive new block : " + block.getNumber());
                        log.info("## Receive new block [{}] : {}", nodeName, block.getNumber());
                        try {
                            que.add(new BlockWrapper(block));
                            for(int i=1; i<10; i++) {
                                que.add(new BlockWrapper(web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(block.getNumber().subtract(BigInteger.valueOf(i))), true).send().getBlock()));
                            }
                            GsonUtil.printGsonPretty(ps,que);
                            ps.println("========================================================================================================================================================================");
                        } catch(IOException e) {
                            log.error("Failed to send ethGetBlockByNumber : " + url, e);
                        }
                    }),
                    (onError -> {
                        log.error("Failed to subscribe block..", onError.getCause());
                    }));
            } catch (Exception e) {
                if(subscription != null && !subscription.isUnsubscribed()) {
                    subscription.unsubscribe();
                }
            }
        };
    }
}