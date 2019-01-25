package collector.ethereum.event;

/**
 * @author zacconding
 * @Date 2018-12-20
 * @GitHub : https://github.com/zacscoding
 */
public abstract class EthereumEvent {

    protected final EthereumEventType eventType;

    public abstract String toSimpleString();

    public EthereumEvent(EthereumEventType eventType) {
        this.eventType = eventType;
    }

    public EthereumEventType getEventType() {
        return eventType;
    }

    public enum EthereumEventType {
        PENDING_TX, BLOCK, TRANSACTION;
    }
}