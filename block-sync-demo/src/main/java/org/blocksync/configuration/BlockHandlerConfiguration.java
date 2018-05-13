package org.blocksync.configuration;

import java.util.ArrayList;
import java.util.List;
import org.blocksync.handler.BlockEventHandler;
import org.blocksync.handler.DumpBlockEventHandler;
import org.blocksync.handler.SyncBlockEventHandler;
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

    @Bean
    public List<BlockEventHandler> blockEventHandlers() {
        List<BlockEventHandler> eventHandlers = new ArrayList<>();

        eventHandlers.add(new DumpBlockEventHandler(blockLogDir));
        eventHandlers.add(new SyncBlockEventHandler(context.getBean(ParityNodeManager.class)));

        return eventHandlers;
    }
}
