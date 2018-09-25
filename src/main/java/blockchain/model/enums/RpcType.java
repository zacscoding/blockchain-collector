package blockchain.model.enums;

import java.util.EnumSet;
import java.util.Set;
import org.springframework.util.StringUtils;

/**
 * @author zacconding
 * @Date 2018-09-26
 * @GitHub : https://github.com/zacscoding
 */
public enum RpcType {

    UNKNOWN, JSON, IPC, WEBSOCKET;

    private static final Set<RpcType> TYPES = EnumSet.allOf(RpcType.class);

    public static RpcType getType(String name) {
        if (StringUtils.hasLength(name)) {
            for (RpcType type : TYPES) {
                if (type.name().equalsIgnoreCase(name)) {
                    return type;
                }
            }
        }

        return UNKNOWN;
    }
}
