package org.blocksync.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Like underscore.js .. working..
 *
 * @author zacconding
 */
public class FunctionalUtil {

    public static <T, V> List<V> map(final List<T> list, final Function<T, V> iteratee) {
        if (isInvalid(list)) {
            return Collections.emptyList();
        }

        List<V> newList = new ArrayList<>(list.size());

        list.forEach(elt -> newList.add(iteratee.apply(elt)));

        return newList;
    }

    /**
     * filter & push new list
     */
    public static <T> List<T> filter(final List<T> list, final Predicate<T> predicate) {
        if (isInvalid(list, predicate)) {
            return Collections.emptyList();
        }

        final List<T> newList = new ArrayList<>();
        for (final T input : list) {
            if (predicate.test(input)) {
                newList.add(input);
            }
        }

        return newList;
    }

    /**
     * find
     *
     * @return find value or ifInvalid
     */
    public static <T> T find(final List<T> list, final Predicate<T> predicate, T ifInvalid) {
        if (isInvalid(list, predicate)) {
            return ifInvalid;
        }

        for (T input : list) {
            if (predicate.test(input)) {
                return input;
            }
        }

        return ifInvalid;
    }


    /**
     * find
     */
    public static <T> T find(final List<T> list, final Predicate<T> predicate) {
        return find(list, predicate, null);
    }

    /**
     * find index of list
     * @return index or -1
     */
    public static <T> int findIndex(final List<T> list, final Predicate<T> predicate) {
        if (isInvalid(list, predicate)) {
            return -1;
        }

        for (int i = 0, len = list.size(); i < len; i++) {
            if (predicate.test(list.get(i))) {
                return i;
            }
        }

        return -1;
    }

    public static <T> boolean some(final List<T> list, final Predicate<T> predicate) {
        return find(list, predicate) != null;
    }

    // more...
    public static <T> boolean every(final List<T> list, final Predicate<T> predicate) {
        if (isInvalid(list, predicate)) {
            return false;
        }

        for (int i = 0, len = list.size(); i < len; i++) {
            if (!predicate.test(list.get(i))) {
                return false;
            }
        }

        return true;
    }

    private static <T> boolean isInvalid(final List<T> list) {
        return (list == null || list.size() == 0);
    }

    private static <T> boolean isInvalid(final List<T> list, final Predicate<T> predicate) {
        return (list == null || list.size() == 0 || predicate == null);
    }
}