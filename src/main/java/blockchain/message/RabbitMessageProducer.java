package blockchain.message;

import blockchain.message.wrapper.ethereum.EthereumBlockWrapper;
import blockchain.message.wrapper.ethereum.EthereumTransactionWrapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * RabbitMQ Message Producer
 *
 * @author zacconding
 * @Date 2018-09-29
 * @GitHub : https://github.com/zacscoding
 */
public class RabbitMessageProducer implements MessageProducer {

    private RabbitTemplate rabbitTemplate;
    private Queue blockQueue;
    private Queue pendingTxQueue;

    public RabbitMessageProducer(RabbitTemplate rabbitTemplate, Queue blockQueue, Queue pendingTxQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.blockQueue = blockQueue;
        this.pendingTxQueue = pendingTxQueue;
    }

    @Override
    public void produceEthereumBlock(EthereumBlockWrapper ethereumBlockWrapper) {
        rabbitTemplate.convertAndSend(blockQueue.getName(), ethereumBlockWrapper);
    }

    @Override
    public void produceEthereumPendingTx(EthereumTransactionWrapper ethereumTransactionWrapper) {
        rabbitTemplate.convertAndSend(pendingTxQueue.getName(), ethereumTransactionWrapper);
    }
}
