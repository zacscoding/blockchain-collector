package collector.util;

import java.util.Collection;
import org.springframework.util.CollectionUtils;

/**
 * @GitHub : https://github.com/zacscoding
 */
public class CollectionUtil extends CollectionUtils {

    /**
     * Getting size or 0 if null
     */
    public static int safeGetSize(Collection<?> collection) {
        return collection == null ? 0 : collection.size();
    }

    private CollectionUtil() {
    }
}
