package blockchain.message;

import blockchain.configuration.condition.RpcTypeEnabledCondition.RabbitMQEnabledCondition;
import blockchain.message.wrapper.ethereum.EthereumBlockWrapper;
import blockchain.message.wrapper.ethereum.EthereumTransactionWrapper;
import blockchain.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

/**
 * Temp consumer for dev
 *
 * @author zacconding
 * @Date 2018-09-29
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j(topic = "message")
@Component
@Conditional(value = RabbitMQEnabledCondition.class)
public class RabbitMessageConsumer {

    @RabbitListener(queues = "#{ethereumBlockQueue.name}")
    public void receiveBlock(EthereumBlockWrapper blockWrapper) {
        log.info("## Receive block.. at rabbitmq consumer\n{} ", GsonUtil.toString(blockWrapper));
    }

    @RabbitListener(queues = "#{ethereumPendingTxQueue.name}")
    public void receivePendingTx(EthereumTransactionWrapper transactionWrapper) {
        log.info("## Receive pending tx.. at rabbitmq consumer\n{} ", GsonUtil.toString(transactionWrapper));
    }
}
