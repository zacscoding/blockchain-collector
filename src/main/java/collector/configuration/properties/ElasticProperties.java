package collector.configuration.properties;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
@ConditionalOnProperty(value = "elasticsearch.enabled", havingValue = "true")
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticProperties {

    private List<String> hosts;
}