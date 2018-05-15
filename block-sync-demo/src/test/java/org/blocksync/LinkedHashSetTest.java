package org.blocksync;

import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;

/**
 * @author zacconding
 * @Date 2018-05-15
 * @GitHub : https://github.com/zacscoding
 */
public class LinkedHashSetTest {

    LinkedHashSet<String> set = new LinkedHashSet<>();

    @Before
    public void setUp() {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        for (int i = 0; i < 100; i++) {
            String parsed = String.format("%06d", i);
            set.add(parsed);
        }
    }

    @Test
    public void pullAndPoll() {
        String prev = null;
        Iterator<String> itr = set.iterator();
        while (itr.hasNext()) {
            String now = itr.next();
            if (prev == null) {
                prev = now;
                continue;
            }

            assertTrue(prev.compareTo(now) < 0);
            prev = now;
        }
    }

    @Test
    public void changeStream() {
        long start = System.currentTimeMillis();
        List<String> pendingResults = set.stream().collect(Collectors.toList());
        long end = System.currentTimeMillis();
        System.out.println("## Parse to list : " + (end - start));

        String prev = null;
        for (String now : pendingResults) {
            if (prev == null) {
                prev = now;
                continue;
            }

            assertTrue(prev.compareTo(now) < 0);
            prev = now;
        }
    }
}