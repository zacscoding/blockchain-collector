package blockchain.configuration;

import blockchain.configuration.condition.RpcTypeEnabledCondition.RabbitMQEnabledCondition;
import blockchain.configuration.properties.RabbitProperties;
import blockchain.message.MessageProducer;
import blockchain.message.RabbitMessageProducer;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ Configuration
 *
 * @author zacconding
 * @Date 2018-09-29
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j(topic = "message")
@Configuration
@Conditional(value = RabbitMQEnabledCondition.class)
@EnableConfigurationProperties(RabbitProperties.class)
public class RabbitConfiguration {

    private RabbitProperties rabbitProperties;

    @Autowired
    public RabbitConfiguration(RabbitProperties rabbitProperties) {
        this.rabbitProperties = rabbitProperties;
    }

    @PostConstruct
    private void setUp() {
        log.info("## >> RabbitMQ is enabled. host : {} , user : {}", rabbitProperties.getHost(), rabbitProperties.getUser());
    }

    /**
     * Ethereum block message queue
     */
    @Bean
    @ConditionalOnProperty(name = "message.rabbitmq.ethereum.enabled", havingValue = "true")
    public Queue ethereumBlockQueue() {
        return new Queue(rabbitProperties.getEthereumQueue().getBlock(), false);
    }

    /**
     * Ethereum pending transaction message queue
     */
    @Bean
    @ConditionalOnProperty(name = "message.rabbitmq.ethereum.enabled", havingValue = "true")
    public Queue ethereumPendingTxQueue() {
        return new Queue(rabbitProperties.getEthereumQueue().getPendingTx(), false);
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();

        factory.setHost(rabbitProperties.getHost());
        factory.setUsername(rabbitProperties.getPassword());
        factory.setPassword(rabbitProperties.getUser());

        return factory;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    /**
     * Message producer
     */
    @Bean
    public MessageProducer messageProducer(RabbitTemplate rabbitTemplate) {
        return new RabbitMessageProducer(rabbitTemplate, ethereumBlockQueue(), ethereumPendingTxQueue());
    }
}