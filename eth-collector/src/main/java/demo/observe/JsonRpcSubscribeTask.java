package demo.observe;

import demo.entity.EthNode;
import demo.entity.enums.SubscribeState;
import demo.entity.enums.RpcType;
import demo.listener.BlockEventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import rx.Subscription;

/**
 * @author zacconding
 * @Date 2018-07-19
 * @GitHub : https://github.com/zacscoding
 */
public class JsonRpcSubscribeTask implements Runnable {

    private final EthNode ethNode;
    private final ObserverManager observerManager;
    private final List<BlockEventListener> blockEventListeners;

    public JsonRpcSubscribeTask(EthNode ethNode, ObserverManager observerManager, List<BlockEventListener> blockEventListeners) {
        this.ethNode = ethNode;
        this.observerManager = observerManager;
        this.blockEventListeners = blockEventListeners;
    }

    @Override
    public void run() {
        RpcType subscribeType = RpcType.getType(ethNode.getType());
        Web3j web3j = null;

        switch (subscribeType) {
            case JSON:
                web3j = Web3j.build(new HttpService(ethNode.getUrl()));
                break;
            case IPC:
            default:
                throw new RuntimeException("Invalid subsribe type : " + subscribeType);
        }

        Subscription subscription = web3j.blockObservable(true).subscribe(ethBlock -> {
            if (blockEventListeners != null) {
                for (BlockEventListener listener : blockEventListeners) {
                    listener.onBlock(ethNode, ethBlock.getBlock());
                }
            }
        }, error -> {
            Map<String, Object> payload = new HashMap<>();
            payload.put("ethNode", ethNode);
            observerManager.nofityState(SubscribeState.ERROR, payload);
        });

        Map<String, Object> payload = new HashMap<>();
        payload.put("ethNode", ethNode);
        payload.put("subscription", subscription);

        observerManager.nofityState(SubscribeState.STARTED, payload);
    }
}