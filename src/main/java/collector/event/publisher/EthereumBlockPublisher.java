package collector.event.publisher;

import collector.event.EthereumBlockEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author zacconding
 * @Date 2018-12-19
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j(topic = "publisher")
@Component
public class EthereumBlockPublisher {

    public void publish(EthereumBlockEvent blockEvent) {
        log.info("## Will publish block event. network : {} / node : {} / block : {}"
            , blockEvent.getNetworkName(), blockEvent.getNodeName(), blockEvent.getBlock().getNumber());
    }
}
