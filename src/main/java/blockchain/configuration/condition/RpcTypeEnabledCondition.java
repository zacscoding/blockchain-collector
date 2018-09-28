package blockchain.configuration.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Rpc type condition
 *
 * - ActiveMQ    : message.type="activemq"
 * - RabbitMQ    : message.type="rabbitmq"
 * - Kafka       : message.type="kafka"
 *
 * @author zacconding
 * @Date 2018-09-29
 * @GitHub : https://github.com/zacscoding
 */
public class RpcTypeEnabledCondition {

    public static class ActiveMQEnabledCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return "activemq".equals(context.getEnvironment().getProperty("message.type"));
        }
    }

    public static class RabbitMQEnabledCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return "rabbitmq".equals(context.getEnvironment().getProperty("message.type"));
        }
    }

    public static class KafkaEnabledCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return "kafka".equals(context.getEnvironment().getProperty("message.type"));
        }
    }
}
