package org.blocksync.configuration;

import ch.qos.logback.classic.Logger;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.blocksync.handler.BlockEventHandler;
import org.blocksync.handler.DisplayBlockMinerHandler;
import org.blocksync.handler.DumpEventHandler;
import org.blocksync.handler.PendingManageEventHandler;
import org.blocksync.handler.SyncCheckHandler;
import org.blocksync.manager.ParityNodeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zacconding
 * @Date 2018-05-13
 * @GitHub : https://github.com/zacscoding
 */

@Slf4j
@Configuration
public class BlockHandlerConfiguration {

    @Autowired
    private ApplicationContext context;

    @Value("${observe.log.path}")
    private String blockLogDir;

    @Value("${handler.dump}")
    private boolean dump;
    @Value("${handler.dump.block}")
    private boolean dumpBlock;
    @Value("${handler.dump.tx}")
    private boolean dumpPendingTx;

    @Value("${handler.miner}")
    private boolean miner;

    @Value("${handler.sync.block}")
    private boolean syncBlock;

    @Value("${handler.sync.pending}")
    private boolean syncPending;

    @Bean
    public List<BlockEventHandler> blockEventHandlers() {
        List<BlockEventHandler> eventHandlers = new ArrayList<>();
        log.info("## =========================== Initialize Handlers ===========================");
        log.info("## Dump : {} (dumpBlock : {}, dumpPendingTx : {})", dump, dumpBlock, dumpPendingTx);
        log.info("## Miner : {}", miner);
        log.info("## Sync Block : {}", syncBlock);
        log.info("## Sync Pending Transaction : {}", syncPending);

        if (dump) {
            eventHandlers.add(new DumpEventHandler(blockLogDir, dumpBlock, dumpPendingTx));
        }

        if (miner) {
            eventHandlers.add(new DisplayBlockMinerHandler());
        }

        if (syncBlock) {
            eventHandlers.add(new SyncCheckHandler(context.getBean(ParityNodeManager.class)));
        }

        if (syncPending) {
            eventHandlers.add(new PendingManageEventHandler(blockLogDir, context.getBean(ParityNodeManager.class)));
        }

        if (eventHandlers.isEmpty()) {
            log.info("## No Handlers in app.");
        }

        return eventHandlers;
    }
}
