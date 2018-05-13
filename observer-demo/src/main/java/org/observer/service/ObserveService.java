package org.observer.service;

import java.util.List;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.observer.configuration.Web3jConfiguration;
import org.observer.handler.BlockEventHandler;
import org.observer.handler.DisplayBlockMinersInform;
import org.observer.handler.DisplayPreviousBlocksHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
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
                // possible to change
                BlockEventHandler blockEventHandler = new DisplayPreviousBlocksHandler();
                // BlockEventHandler blockEventHandler = new DisplayBlockMinersInform();

                // subscribe block
                subscription = web3j.blockObservable(true).subscribe(
                    (onNext -> {
                        blockEventHandler.handleBlock(url, onNext.getBlock(), web3j);
                    }),
                    (onError -> {
                        blockEventHandler.handleError(onError);
                    }));
            } catch (Exception e) {
                if(subscription != null && !subscription.isUnsubscribed()) {
                    subscription.unsubscribe();
                }
            }
        };
    }
}