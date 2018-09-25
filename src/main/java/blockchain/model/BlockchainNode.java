package blockchain.model;

import blockchain.model.enums.BlockchainType;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author zacconding
 * @Date 2018-09-26
 * @GitHub : https://github.com/zacscoding
 */
@Getter
@Setter
@ToString
public class BlockchainNode {

    private BlockchainType blockchainType;
    private long blockTime;

    private String nodeName;
    private Rpc rpc;
    private Subscribe subscribe;

    @Override
    public int hashCode() {
        return nodeName.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BlockchainNode)) {
            return false;
        }
        BlockchainNode that = (BlockchainNode) o;
        return Objects.equals(getNodeName(), that.getNodeName());
    }

    @Getter
    @Setter
    @ToString
    public static class Rpc {

        private String type;
        private String url;
    }

    @Getter
    @Setter
    @ToString
    public static class Subscribe {

        private boolean block;
        private boolean pendingTx;
    }

}
