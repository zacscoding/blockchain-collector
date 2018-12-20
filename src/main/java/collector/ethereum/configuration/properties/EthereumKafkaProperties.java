package collector.ethereum.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Ethereum kafka properties
 *
 * @author zacconding
 */
@Slf4j(topic = "properties")
@Getter
@Setter
@Component
@ConditionalOnProperty(name = "blockchain.eth.enabled", havingValue = "true")
@ConfigurationProperties(prefix = "kafka.eth")
public class EthereumKafkaProperties {

    private Topic topic;

    @Getter
    @Setter
    public static final class Topic {

        private String block;
        private String tx;
        private String pendingTx;
    }
}
