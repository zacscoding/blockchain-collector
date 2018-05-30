package org.blocksync.configuration;

import ch.qos.logback.classic.Logger;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.blocksync.handler.BlockEventHandler;
import org.blocksync.handler.CheckBlockHashEventHandler;
import org.blocksync.handler.CheckHashesEventHandler;
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
    @Value("${handler.same.hash}")
    private boolean checkSameHash;


    @Value("${handler.sync.pending}")
    private boolean syncPending;

    @Value("${handler.compare.hash}")
    private boolean compareHash;

    @Bean
    public List<BlockEventHandler> blockEventHandlers() {
        List<BlockEventHandler> eventHandlers = new ArrayList<>();
        log.info("## =========================== Initialize Handlers ===========================");
        log.info("## Dump : {} (dumpBlock : {}, dumpPendingTx : {})", dump, dumpBlock, dumpPendingTx);
        log.info("## Miner : {}", miner);
        log.info("## Sync Block : {}", syncBlock);
        log.info("## Check same hash : {}", checkSameHash);
        log.info("## Sync Pending Transaction : {}", syncPending);
        log.info("## Compare hash between two chain : {}", compareHash);

        ParityNodeManager parityNodeManager = context.getBean(ParityNodeManager.class);

        if (dump) {
            eventHandlers.add(new DumpEventHandler(blockLogDir, dumpBlock, dumpPendingTx));
        }

        if (miner) {
            eventHandlers.add(new DisplayBlockMinerHandler(blockLogDir));
        }

        if (syncBlock) {
            eventHandlers.add(new SyncCheckHandler(parityNodeManager, blockLogDir));
        }

        if (syncPending) {
            eventHandlers.add(new PendingManageEventHandler(blockLogDir, parityNodeManager));
        }

        if(checkSameHash) {
            eventHandlers.add(new CheckBlockHashEventHandler(blockLogDir, parityNodeManager));
        }

        if(compareHash) {
            eventHandlers.add(new CheckHashesEventHandler(blockLogDir, parityNodeManager));
        }

        if (eventHandlers.isEmpty()) {
            log.info("## No Handlers in app.");
        }

        return eventHandlers;
    }
}
