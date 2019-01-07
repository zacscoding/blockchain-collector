package collector.configuration.properties;

import collector.util.GsonUtil;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zacconding
 */
@Slf4j(topic = "properties")
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kafka.common")
public class KafkaProperties {

    private Bootstrap bootstrap;

    @PostConstruct
    private void setUp() {
        log.info("## kafka properties ===================================================");
        log.debug(GsonUtil.toStringPretty(this));
        log.info("=======================================================================");
    }

    @Getter
    @Setter
    public static final class Bootstrap {

        private String servers;
    }
}
