package blockchain.model.enums;

import java.util.EnumSet;
import java.util.Set;
import org.springframework.util.StringUtils;

/**
 * @author zacconding
 * @Date 2018-09-26
 * @GitHub : https://github.com/zacscoding
 */
public enum ClientType {

    UNKNOWN, ETH_GO, PARITY;

    private static final Set<ClientType> TYPES = EnumSet.allOf(ClientType.class);

    public static ClientType getType(String name) {
        if (StringUtils.hasLength(name)) {
            for (ClientType type : TYPES) {
                if (type.name().equalsIgnoreCase(name)) {
                    return type;
                }
            }
        }

        return UNKNOWN;
    }
}
