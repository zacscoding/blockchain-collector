package collector.ethereum.configuration.properties;

import collector.ethereum.EthereumNetwork;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Ethereum node properties
 *
 * @author zacconding
 * @Date 2018-12-19
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j(topic = "properties")
@Getter
@Setter
@Component
@ConditionalOnProperty(name = "blockchain.eth.enabled", havingValue = "true")
@ConfigurationProperties(prefix = "nodes.eth")
public class EthereumProperties {

    List<EthereumNetwork> networks;
}
