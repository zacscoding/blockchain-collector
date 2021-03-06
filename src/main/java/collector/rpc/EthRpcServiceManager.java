package collector.rpc;

import collector.network.ethereum.EthereumNode;
import collector.configuration.EthConfiguration;
import collector.util.OSUtil;
import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.filters.BlockFilter;
import org.web3j.protocol.core.filters.PendingTransactionFilter;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.ipc.UnixIpcService;
import org.web3j.protocol.ipc.WindowsIpcService;
import org.web3j.protocol.websocket.WebSocketService;
import org.web3j.utils.Async;

/**
 * Ethereum rpc service manager
 *
 * @author zacconding
 */
@Slf4j(topic = "rpc")
@Component
@ConditionalOnBean(value = EthConfiguration.class)
public class EthRpcServiceManager {

    private final Object lock = new Object();
    private Map<String, Web3j> web3jMap = new HashMap<>();
    private Map<String, BlockFilter> blockFilterMap = new ConcurrentHashMap<>();
    private Map<String, PendingTransactionFilter> pendingTxFilterMap = new ConcurrentHashMap<>();

    /**
     * start Block filter
     */
    public boolean registerBlockFilter(EthereumNode ethereumNode, long blockTime, Consumer<Block> onBlock) {

        Objects.requireNonNull(ethereumNode, "ethereumNode must be not null");
        Objects.requireNonNull(onBlock, "onBlock must be not null");
        Assert.isTrue(blockTime > 0L, "block time must be larger than 0");

        final Web3j web3j = getOrCreateWeb3j(ethereumNode, blockTime);
        if (web3j == null) {
            logger.warn("Failed to register eth filter because can`t create web3j service");
            return false;
        }

        BlockFilter blockFilter = new BlockFilter(web3j, hash -> {
            try {
                Block block = web3j.ethGetBlockByHash(hash, true).send().getBlock();
                onBlock.accept(block);
            } catch (IOException e) {
                logger.warn("Failed to get block by hash " + hash, e);
            }
        });

        blockFilter.run(Executors.newSingleThreadScheduledExecutor(), blockTime);
        blockFilterMap.put(ethereumNode.getNodeName(), blockFilter);
        return true;
    }

    /**
     * Checks whether started block filter or not
     */
    public boolean hasBlockFilter(EthereumNode ethereumNode) {
        if (ethereumNode == null || ethereumNode.getNodeName() == null) {
            return false;
        }

        return blockFilterMap.containsKey(ethereumNode.getNodeName());
    }

    /**
     * Cancle block filter
     *
     * @return true : success to cancel, false if not exist filter
     */
    public boolean cancelBlockFilter(EthereumNode ethereumNode) {
        BlockFilter filter = blockFilterMap.remove(ethereumNode.getNodeName());
        if (filter == null) {
            return false;
        }

        filter.cancel();
        return true;
    }

    /**
     * Register pending tx filter
     *
     * @return true : success to register , false : already registered or can`t create web3j
     */
    public boolean registerPendingTxFilter(EthereumNode ethereumNode, long blockTime,
        long pollingInterval, Consumer<Transaction> onPendingTx) {

        Objects.requireNonNull(ethereumNode, "ethereumNode must be not null");
        Objects.requireNonNull(onPendingTx, "onBlock must be not null");
        Assert.isTrue(blockTime > 0L, "blockTime must be larger than 0");
        Assert.isTrue(pollingInterval > 0L, "pollingInterval must be larger than 0");

        PendingTransactionFilter pendingTxFilter = pendingTxFilterMap.get(ethereumNode.getNodeName());

        if (pendingTxFilter != null) {
            logger.warn("Already registered pending tx filter : {}", ethereumNode.getNodeName());
            return false;
        }

        final Web3j web3j = getOrCreateWeb3j(ethereumNode, blockTime);
        if (web3j == null) {
            logger.warn("Failed to register eth filter because can`t create web3j service");
            return false;
        }

        pendingTxFilter = new PendingTransactionFilter(web3j, hash -> {
            try {
                web3j.ethGetTransactionByHash(hash).send().getTransaction().ifPresent(
                    tx -> onPendingTx.accept(tx)
                );
            } catch (IOException e) {
                logger.warn("IOException occur while getting pending tx : " + hash, e);
            }
        });

        pendingTxFilter.run(Executors.newSingleThreadScheduledExecutor(), pollingInterval);
        pendingTxFilterMap.put(ethereumNode.getNodeName(), pendingTxFilter);

        return true;
    }

    /**
     * Checks whether started pending tx filter or not
     */
    public boolean hasPendingTxFilter(EthereumNode ethereumNode) {
        if (ethereumNode == null || ethereumNode.getNodeName() == null) {
            return false;
        }

        return pendingTxFilterMap.containsKey(ethereumNode.getNodeName());
    }

    /**
     * Cancel pending tx filter
     *
     * @return true : success to cancel, false : if not exist
     */
    public boolean cancelPendingTxFilter(EthereumNode ethereumNode) {
        PendingTransactionFilter filter = pendingTxFilterMap.remove(ethereumNode.getNodeName());

        if (filter == null) {
            return false;
        }

        filter.cancel();
        return true;
    }

    /**
     * Get or create web3j instance
     */
    public Web3j getOrCreateWeb3j(EthereumNode ethereumNode, long blockTime) {
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
                logger.warn("Failed to connect " + ethereumNode.getWebSocketUrl());
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
