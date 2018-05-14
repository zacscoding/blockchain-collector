package org.blocksync.configuration;

import java.util.ArrayList;
import java.util.List;
import org.blocksync.handler.BlockEventHandler;
import org.blocksync.handler.DisplayBlockMinerHandler;
import org.blocksync.handler.DumpEventHandler;
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

@Configuration
public class BlockHandlerConfiguration {

    @Autowired
    private ApplicationContext context;

    @Value("${observe.log.path}")
    private String blockLogDir;

    @Value("${dump.block}")
    private boolean dumpBlock;

    @Value("${dump.pending.tx}")
    private boolean dumpPendingTx;

    @Bean
    public List<BlockEventHandler> blockEventHandlers() {
        List<BlockEventHandler> eventHandlers = new ArrayList<>();

        eventHandlers.add(new DumpEventHandler(blockLogDir, dumpBlock, dumpPendingTx));
        // eventHandlers.add(new DisplayBlockMinerHandler());
        // eventHandlers.add(new SyncCheckHandler(context.getBean(ParityNodeManager.class)));

        return eventHandlers;
    }
}
