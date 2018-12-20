package collector.configuration;

import collector.ethereum.EthereumNetwork;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zacconding
 * @Date 2018-12-19
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j(topic = "properties")
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "nodes.eth")
public class EthereumProperties {

    List<EthereumNetwork> networks;

    // TEMP FOR DEBUG
    @PostConstruct
    private void setUp() {
        try {
            log.info("## Ethereum Properties ##\n{}\n============================================",
                new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(networks)
            );
        } catch (Exception e) {

        }
    }
    // -- TEMP FOR DEBUG
}
