package demo.observe;

import demo.entity.enums.SubscribeState;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author zacconding
 * @Date 2018-07-19
 * @GitHub : https://github.com/zacscoding
 */
@Component
public class ObserverManager {

    private static final Logger logger = LoggerFactory.getLogger("observe");

    public void nofityState(SubscribeState state, Map<String, Object> payload) {

    }

    private void initialize() {

    }
}