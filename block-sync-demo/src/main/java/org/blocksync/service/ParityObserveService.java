package org.blocksync.service;

import java.util.List;
import java.util.function.Function;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.blocksync.entity.Node;
import org.blocksync.handler.BlockEventHandler;
import org.blocksync.manager.NodeManager;
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
    private Thread[] blockObservers;
    private Thread[] pendingObservers;

    @PostConstruct
    private void startSubscribe() {
        log.info("Start to initialize.");

        blockSubscribe(parityNodeManager.getNodes());
        pendingTransactionSubscribe(parityNodeManager.getNodes());
    }

    private void blockSubscribe(List<Node> nodes) {
        blockObservers = new Thread[nodes.size()];

        Function<Node, Runnable> createTask = (Node node) -> () -> {
            Subscription subscription = null;
            try {
                log.info("## Try to subscribe blocks " + node);
                Web3j web3j = parityNodeManager.getWeb3jFromName(node.getName());

                subscription = web3j.blockObservable(true).subscribe(
                    ethBlock -> {
                        blockEventHandlers.forEach(handler -> eventHandlerExecutor.execute(() -> handler.onBlock(node, ethBlock)));
                    },
                    error -> {
                        error.printStackTrace();
                    }
                );
            } catch (Exception e) {
                if(subscription != null && !subscription.isUnsubscribed()) {
                    subscription.unsubscribe();
                }
            }
        };

        subscribeInternal("block", nodes, pendingObservers, createTask);
    }

    private void pendingTransactionSubscribe(List<Node> nodes) {
        pendingObservers = new Thread[nodes.size()];

        Function<Node, Runnable> createTask = (Node node) -> () -> {
            Subscription subscription = null;
            try {
                log.info("## Try to subscribe pending txns " + node);
                Web3j web3j = parityNodeManager.getWeb3jFromName(node.getName());

                subscription = web3j.pendingTransactionObservable().subscribe(
                    tx -> {
                        blockEventHandlers.forEach(handler -> eventHandlerExecutor.execute(() -> handler.onPendingTransaction(node, tx)));
                    },
                    error -> {
                        error.printStackTrace();
                    }
                );
            } catch (Exception e) {
                if(subscription != null && !subscription.isUnsubscribed()) {
                    subscription.unsubscribe();
                }
            }
        };

        subscribeInternal("pending-txns", nodes, pendingObservers, createTask);
    }

    private void subscribeInternal(String prefix, List<Node> nodes, Thread[] observers, Function<Node, Runnable> createTask) {
        if(observers == null) {
            observers = new Thread[nodes.size()];
        }

        for (int i = 0; i < nodes.size(); i++) {
            Node node = parityNodeManager.getNode(i);
            Thread t = new Thread(createTask.apply(node));
            t.setName("Observer - "+ prefix + "[" + node + "]");
            t.setDaemon(true);
            observers[i] = t;
            t.start();
        }
    }
}
