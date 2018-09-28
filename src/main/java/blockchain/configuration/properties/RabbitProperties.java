package blockchain.configuration.properties;

import blockchain.configuration.condition.RpcTypeEnabledCondition.RabbitMQEnabledCondition;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ properties
 *
 * @author zacconding
 * @Date 2018-09-29
 * @GitHub : https://github.com/zacscoding
 */
@Getter
@Setter
@Component
@Conditional(value = RabbitMQEnabledCondition.class)
@ConfigurationProperties(prefix = "message.rabbitmq")
public class RabbitProperties {

    private String host;
    private String user;
    private String password;
    private EthereumQueue ethereumQueue;

    @Getter
    @Setter
    public static class EthereumQueue {

        private String block;
        private String pendingTx;
    }
}