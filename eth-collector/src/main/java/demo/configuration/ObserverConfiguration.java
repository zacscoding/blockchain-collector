package demo.configuration;

import demo.entity.EthNode;
import demo.listener.BlockEventListener;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zacconding
 * @Date 2018-07-19
 * @GitHub : https://github.com/zacscoding
 */
@Configuration
public class ObserverConfiguration {

    @Autowired
    public ObserverConfiguration(CollectorProperties properties) {
        for (EthNode ethNode : properties.getNodes()) {
            System.out.println(ethNode);
        }
    }

    @Bean
    public List<BlockEventListener> blockEventListeners() {
        return null;
    }
}