package collector.ethereum.rpc;

import collector.ethereum.EthereumNode;
import collector.util.OSUtil;
import java.io.IOException;
import java.net.ConnectException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.filters.BlockFilter;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.ipc.UnixIpcService;
import org.web3j.protocol.ipc.WindowsIpcService;
import org.web3j.protocol.websocket.WebSocketService;
import org.web3j.utils.Async;

/**
 * Ethereum rpc service manager
 *
 * @author zacconding
 * @Date 2018-12-19
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j(topic = "rpc")
@Component
public class EthereumRpcServiceManager {

    private final Object lock = new Object();
    private Map<String, Web3j> web3jMap = new ConcurrentHashMap<>();
    private Map<String, BlockFilter> blockFilterMap = new ConcurrentHashMap<>();

    /**
     * start Block filter
     */
    public boolean registerBlockFilter(EthereumNode ethereumNode, long blockTime, Consumer<Block> onBlock) {

        Objects.requireNonNull(ethereumNode, "ethereumNode must be not null");
        Objects.requireNonNull(onBlock, "onBlock must be not null");

        if (blockTime <= 0L) {
            log.warn("Invalid block time. {} block time must larger than 0", blockTime);
            return false;
        }

        final Web3j web3j = createWeb3j(ethereumNode, blockTime);
        if (web3j == null) {
            log.warn("Failed to register eth filter because can`t create web3j service");
            return false;
        }

        BlockFilter blockFilter = new BlockFilter(web3j, hash -> {
            try {
                Block block = web3j.ethGetBlockByHash(hash, true).send().getBlock();
                onBlock.accept(block);
            } catch (IOException e) {
                log.warn("Failed to get block by hash " + hash, e);
            }
        });

        blockFilter.run(Executors.newSingleThreadScheduledExecutor(), blockTime);
        blockFilterMap.put(ethereumNode.getNodeName(), blockFilter);
        return true;
    }

    /**
     * Cancle block filter
     *
     * @return true : success to cancel, false if not exist filter
     */
    public boolean cancelBlockFilter(EthereumNode ethereumNode) {
        BlockFilter filter = blockFilterMap.get(ethereumNode.getNodeName());
        if (filter == null) {
            return false;
        }

        filter.cancel();
        return true;
    }

    private Web3j createWeb3j(EthereumNode ethereumNode, long blockTime) {
        Web3j web3j = web3jMap.get(ethereumNode.getNodeName());

        if (web3j == null) {
            synchronized (lock) {
                if ((web3j = web3jMap.get(ethereumNode.getNodeName())) == null) {
                    Web3jService web3jService = createWeb3jService(ethereumNode);
                    if (web3jService != null) {
                        web3j = Web3j.build(web3jService, blockTime, Async.defaultExecutorService());
                        web3jMap.put(ethereumNode.getNodeName(), web3j);
                    }
                }
            }
        }

        return web3j;
    }

    private Web3jService createWeb3jService(EthereumNode ethereumNode) {
        if (StringUtils.hasText(ethereumNode.getIpcPath())) {
            return createIpcService(ethereumNode.getIpcPath());
        }

        if (StringUtils.hasText(ethereumNode.getWebSocketUrl())) {
            try {
                return createWebSocketService(ethereumNode.getWebSocketUrl());
            } catch (ConnectException e) {
                log.warn("Failed to connect " + ethereumNode.getWebSocketUrl());
                return null;
            }
        }

        if (StringUtils.hasText(ethereumNode.getHttpUrl())) {
            return createHttpService(ethereumNode.getHttpUrl());
        }

        return null;
    }

    private Web3jService createIpcService(String ipcPath) {
        if (OSUtil.isWindows()) {
            return new WindowsIpcService(ipcPath);
        }

        return new UnixIpcService(ipcPath);
    }

    private Web3jService createWebSocketService(String webSocketUrl) throws ConnectException {
        WebSocketService webSocketService = new WebSocketService(webSocketUrl, false);
        webSocketService.connect();
        return webSocketService;
    }

    private Web3jService createHttpService(String httpUrl) {
        return new HttpService(httpUrl);
    }
}