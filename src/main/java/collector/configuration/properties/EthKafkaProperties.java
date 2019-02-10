package collector.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
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
@ConditionalOnBean(KafkaProperties.class)
@ConditionalOnProperty(name = "kafka.eth.enabled", havingValue = "true")
@ConfigurationProperties(prefix = "kafka.eth")
public class EthKafkaProperties {

    private Topic topic;

    @Autowired
    private void setup() {
        System.out.println("EthereumKafkaProperties is generated..");
    }

    @Getter
    @Setter
    public static final class Topic {

        private String block;
        private String tx;
        private String pendingTx;
    }
}
