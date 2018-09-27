package blockchain.observe.listener;

import blockchain.model.enums.BlockchainType;
import java.util.List;
import org.springframework.util.CollectionUtils;

/**
 * @author zacconding
 * @Date 2018-09-27
 * @GitHub : https://github.com/zacscoding
 */
public class BlockchainListener {

    private List<EthereumListener> ethereumListeners;

    public BlockchainListener(List<EthereumListener> ethereumListeners) {
        this.ethereumListeners = ethereumListeners;
    }

    public List<EthereumListener> getEthereumListeners() {
        return ethereumListeners;
    }

    public boolean hasListeners(BlockchainType blockchainType) {
        switch (blockchainType) {
            case ETHEREUM:
                return !CollectionUtils.isEmpty(ethereumListeners);
            case BITCOIN:
            case UNKNOWN:
            default:
                return false;
        }
    }
}