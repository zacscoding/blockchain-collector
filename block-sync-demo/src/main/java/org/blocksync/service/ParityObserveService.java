package org.blocksync.service;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.blocksync.entity.Node;
import org.blocksync.handler.BlockEventHandler;
import org.blocksync.handler.DumpBlockEventHandler;
import org.blocksync.handler.SyncBlockEventHandler;
import org.blocksync.manager.ParityNodeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import rx.Subscription;

/**
 * @author zacconding
 * @Date 2018-05-12
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
@Service
public class ParityObserveService {

    @Autowired
    private TaskExecutor eventHandlerExecutor;
    @Autowired
    private ParityNodeManager parityNodeManager;
    @Autowired
    private List<BlockEventHandler> blockEventHandlers;
    private Thread[] observers;

    @PostConstruct
    private void startSubscribe() {
        log.info("Start to initialize.");

        List<Node> nodes = parityNodeManager.getNodes();

        observers = new Thread[nodes.size()];

        for (int i = 0; i < nodes.size(); i++) {
            Node node = parityNodeManager.getNode(i);
            Thread t = new Thread(createTask(node));
            t.setName("Observer[" + node.getName() + ":" + node.getUrl() + "]");
            t.setDaemon(true);
            observers[i] = t;
            t.start();
        }
    }

    private Runnable createTask(final Node node) {
        return () -> {
            while(!Thread.currentThread().isInterrupted()) {
                Subscription subscription = null;
                try {
                    log.info("## Try to subscribe " + node);
                    final Web3j web3j = Web3j.build(new HttpService(node.getUrl()));

                    // subscribe block eventHandlerExecutor
                    subscription = web3j.blockObservable(true).subscribe(
                        (onNext -> blockEventHandlers.forEach(handler -> eventHandlerExecutor.execute(() -> handler.onBlock(node, onNext)))),
                        (onError -> log.error("Failed to subscribe " + node, onError))
                    );

                    break;
                } catch (Exception e) {
                    if(subscription != null && !subscription.isUnsubscribed()) {
                        subscription.unsubscribe();
                    }
                }
            }
        };
    }
}
