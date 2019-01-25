package collector.ethereum.event.publisher;

import collector.ethereum.configuration.EthereumConfiguration;
import collector.ethereum.event.EthereumPendingTxEvent;
import collector.util.CollectorThreadFactory;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import java.util.Objects;
import java.util.concurrent.Executors;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * @author zacconding
 * @Date 2018-12-20
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j(topic = "publisher")
@Component
@ConditionalOnBean(value = EthereumConfiguration.class)
public class EthereumPendingTxPublisher {

    private EventBus asyncEventBus;

    @PostConstruct
    private void setUp() {
        CollectorThreadFactory factory = new CollectorThreadFactory("pending-tx-publisher", true);
        this.asyncEventBus = new AsyncEventBus("pending-tx-event-bus", Executors.newCachedThreadPool(factory));
    }

    public void publish(EthereumPendingTxEvent event) {
        asyncEventBus.post(event);
    }

    public void register(Object listener) {
        Objects.requireNonNull(listener, "listener must be not null");
        asyncEventBus.register(listener);
        logger.debug("## Success to register listener : {} at Eth Pending Tx Publisher"
            , listener.getClass().getSimpleName());
    }
}
