package org.blocksync;

import java.math.BigInteger;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import org.junit.Test;

/**
 * @author zacconding
 * @Date 2018-05-13
 * @GitHub : https://github.com/zacscoding
 */
public class BlockingQueueTest {

    @Test
    public void blockingQueueTest() {
        PriorityBlockingQueue<BigInteger> que = new PriorityBlockingQueue<>(11, Collections.reverseOrder());

        que.offer(BigInteger.valueOf(1));
        que.offer(BigInteger.valueOf(5));
        que.offer(BigInteger.valueOf(3));
        que.offer(BigInteger.valueOf(15));

        try {
            while (!que.isEmpty()) {
                System.out.println("Poll : " + que.take());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
