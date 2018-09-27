package blockchain.observe;

import blockchain.model.BlockchainNode;
import blockchain.model.BlockchainNode.Subscribe;
import blockchain.model.enums.BlockchainType;
import blockchain.observe.listener.BlockchainListener;
import blockchain.observe.listener.EthereumListener;
import blockchain.rpc.RpcServiceManager;
import blockchain.rpc.RpcServices;
import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;

/**
 * @author zacconding
 * @Date 2018-09-26
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j(topic = "subscribe")
@Component
public class SubscribeManager {

    private List<BlockchainNode> blockchainNodes;
    private BlockchainListener blockchainListener;
    private RpcServiceManager rpcServiceManager;
    private TaskExecutor taskExecutor;

    @Autowired
    public SubscribeManager(List<BlockchainNode> blockchainNodes, BlockchainListener blockchainListener, RpcServiceManager rpcServiceManager,
        @Qualifier("eventHandlerExecutor") TaskExecutor taskExecutor) {
        this.blockchainNodes = blockchainNodes;
        this.blockchainListener = blockchainListener;
        this.rpcServiceManager = rpcServiceManager;
        this.taskExecutor = taskExecutor;
    }

    @PostConstruct
    private void setUp() {
        for (BlockchainNode blockchainNode : blockchainNodes) {
            subscribe(blockchainNode);
        }
    }

    public void subscribe(BlockchainNode blockchainNode) {
        Objects.requireNonNull(blockchainNode, "blockchainNode must be not null");

        switch (blockchainNode.getBlockchainType()) {
            case ETHEREUM:
                subscribeEthereum(blockchainNode);
                break;
            default:
                throw new UnsupportedOperationException("Not supported blockchain typy : " + blockchainNode.getBlockchainType());
        }
    }

    private void subscribeEthereum(BlockchainNode blockchainNode) {
        if (!blockchainListener.hasListeners(BlockchainType.ETHEREUM)) {
            return;
        }

        List<EthereumListener> ethereumListeners = blockchainListener.getEthereumListeners();
        Subscribe subscribe = blockchainNode.getSubscribe();
        boolean isSubscribe = subscribe.isBlock() || subscribe.isPendingTx();

        if (!isSubscribe) {
            return;
        }

        RpcServices rpcServices = rpcServiceManager.getRpcServices(blockchainNode);
        Web3j web3j = rpcServices.getWeb3j();

        if (subscribe.isBlock()) {
            web3j.blockObservable(true).subscribe(onNext -> {
                ethereumListeners.forEach(listener -> taskExecutor.execute(() -> listener.onBlock(blockchainNode, onNext.getBlock())));
            }, (onError -> {
                log.warn("Failed to subscribe block.. " + onError);
                // TODO :: handle error
            }));
        }

        if (subscribe.isPendingTx()) {
            web3j.pendingTransactionObservable().subscribe(onNext -> {
                ethereumListeners.forEach(listener -> taskExecutor.execute(() -> listener.onPendingTransaction(blockchainNode, onNext)));
            }, onError -> {
                log.warn("Failed to subscribe pending tx... ", onError);
            });
        }
    }
}