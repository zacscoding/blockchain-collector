package demo.entity.enums;

import java.util.EnumSet;
import java.util.Set;
import org.springframework.util.StringUtils;

/**
 * @author zacconding
 * @Date 2018-07-19
 * @GitHub : https://github.com/zacscoding
 */
public enum RpcType {
    UNKNOWN, IPC, JSON, WEBSOCKET;

    private static final Set<RpcType> TYPES = EnumSet.allOf(RpcType.class);

    public static RpcType getType(String name) {
        if (StringUtils.hasText(name)) {
            for (RpcType type : TYPES) {
                if (type.name().equalsIgnoreCase(name)) {
                    return type;
                }
            }
        }

        return UNKNOWN;
    }
}
