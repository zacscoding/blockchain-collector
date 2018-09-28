package blockchain.rpc;

import blockchain.model.BlockchainNode;
import blockchain.model.BlockchainNode.Rpc;
import blockchain.model.enums.BlockchainType;
import blockchain.model.enums.RpcType;
import blockchain.util.Async;
import blockchain.util.OSUtil;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.ipc.UnixIpcService;
import org.web3j.protocol.ipc.WindowsIpcService;
import org.web3j.protocol.websocket.WebSocketClient;
import org.web3j.protocol.websocket.WebSocketService;

/**
 * RpcServices manager
 *
 * @author zacconding
 * @Date 2018-09-26
 * @GitHub : https://github.com/zacscoding
 */
@Component
public class RpcServiceManager {

    private Object lock;
    private ConcurrentHashMap<BlockchainNode, RpcServices> rpcServices;

    @PostConstruct
    private void setUp() {
        this.lock = new Object();
        this.rpcServices = new ConcurrentHashMap<>();
    }

    public RpcServices getRpcServices(BlockchainNode blockchainNode) {
        RpcServices services = rpcServices.get(blockchainNode);

        if (services == null) {
            synchronized (lock) {
                if ((services = rpcServices.get(blockchainNode)) == null) {
                    switch (blockchainNode.getBlockchainType()) {
                        case ETHEREUM:
                            services = createEthereumRpcServices(blockchainNode);
                            break;
                        default:
                            throw new UnsupportedOperationException("Not supported blockchain typy : " + blockchainNode.getBlockchainType());
                    }

                    rpcServices.put(blockchainNode, services);
                }
            }
        }

        return services;
    }

    private RpcServices createEthereumRpcServices(BlockchainNode blockchainNode) {
        RpcServices rpcServices = new RpcServices();
        rpcServices.setBlockchainType(BlockchainType.ETHEREUM);

        Rpc rpc = blockchainNode.getRpc();
        RpcType rpcType = RpcType.getType(rpc.getType());
        String url = rpc.getUrl();

        if (!StringUtils.hasText(url)) {
            throw new RuntimeException("Invalid rpc url : " + url);
        }

        Web3jService web3jService = null;
        switch (rpcType) {
            case IPC:
                web3jService = (OSUtil.isWindows()) ? new WindowsIpcService(url) : new UnixIpcService(url);
                break;
            case JSON:
                web3jService = new HttpService(url);
                break;
            case WEBSOCKET:
                try {
                    WebSocketClient client = new WebSocketClient(new URI(url));
                    WebSocketService webSocketService = new WebSocketService(client, false);
                    webSocketService.connect();
                    web3jService = webSocketService;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case UNKNOWN:
                throw new RuntimeException("Invalid rpc type : " + rpc.getType());
        }

        rpcServices.setWeb3jService(web3jService);
        // Web3j for block observe
        rpcServices.setDefaultWeb3j(Web3j.build(web3jService, blockchainNode.getBlockTime(), Async.defaultExecutorService()));
        // Web3j for pending tx observe
        rpcServices.setShortPollingWeb3j(Web3j.build(web3jService, 500L, Async.executorService(Executors.newSingleThreadScheduledExecutor())));

        return rpcServices;
    }
}
