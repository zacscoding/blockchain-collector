package collector.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * @GitHub : https://github.com/zacscoding
 */
@ConditionalOnBean(value = {ElasticsearchConfiguration.class})
@ConditionalOnProperty(name = "elasticsearch.eth.enabled", havingValue = "true")
@Configuration
public class EthElasticConfiguration {
}
