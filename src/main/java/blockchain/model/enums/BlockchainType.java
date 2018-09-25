package blockchain.model.enums;

import java.util.EnumSet;
import java.util.Set;
import org.springframework.util.StringUtils;

/**
 * @author zacconding
 * @Date 2018-09-26
 * @GitHub : https://github.com/zacscoding
 */
public enum BlockchainType {

    UNKNOWN, ETHEREUM, BITCOIN;

    private static final Set<BlockchainType> TYPES = EnumSet.allOf(BlockchainType.class);

    public static BlockchainType getType(String name) {
        if (StringUtils.hasLength(name)) {
            for (BlockchainType type : TYPES) {
                if (type.name().equalsIgnoreCase(name)) {
                    return type;
                }
            }
        }

        return UNKNOWN;
    }
}