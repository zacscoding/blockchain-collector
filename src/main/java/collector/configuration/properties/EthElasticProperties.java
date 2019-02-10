package collector.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j(topic = "properties")
@Getter
@Setter
@Component
@ConditionalOnBean(ElasticProperties.class)
@ConditionalOnProperty(name = "elasticsearch.eth.enabled", havingValue = "true")
@ConfigurationProperties(prefix = "elasticsearch.eth")
public class EthElasticProperties {

    private boolean rollingRule;
    private int rollingBlockNumber;
    private int rollingTxNumber;
}
