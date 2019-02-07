package collector.configuration;

import collector.configuration.properties.EthereumProperties;
import collector.util.GsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * @author zacconding
 */
@Slf4j(topic = "config")
@ConditionalOnProperty(name = "blockchain.eth.enabled", havingValue = "true")
@Configuration
public class EthereumConfiguration {

    // TEMP FOR DEBUG
    @Autowired
    public EthereumConfiguration(EthereumProperties properties) {
        try {
            logger.info("## Ethereum Properties ##\n{}\n============================================",
                GsonUtil.toStringPretty(properties)
            );
        } catch (Exception e) {
            logger.warn("Failed to parse ethereum properties : ", e);
        }
    }
    // -- TEMP FOR DEBUG
}