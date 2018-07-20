package org.blocksync;

import java.math.BigInteger;
import org.blocksync.util.SimpleLogger;
import org.junit.Before;
import org.junit.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.http.HttpService;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author zacconding
 * @Date 2018-06-03
 * @GitHub : https://github.com/zacscoding
 */
public class BlockGasLimitTest {

    Web3j web3j;
    @Before
    public void setUp() {
        //web3j = Web3j.build(new HttpService("http://192.168.79.128:8540/"));
        web3j = Web3j.build(new HttpService("http://192.168.5.77:8541/"));
    }

    /*
    ## Receive block : 0, gasLimit : 6000000, gasUsed : 0==> #tx : 0
    ## Receive block : 1, gasLimit : 5994142, gasUsed : 2373000==> #tx : 113
    ## Receive block : 2, gasLimit : 5988290, gasUsed : 5061000==> #tx : 241
    ## Receive block : 3, gasLimit : 5982444, gasUsed : 5061000==> #tx : 241
    ## Receive block : 4, gasLimit : 5976603, gasUsed : 5040000==> #tx : 240
    ## Receive block : 5, gasLimit : 5970768, gasUsed : 5040000==> #tx : 240
    ## Receive block : 6, gasLimit : 5964939, gasUsed : 5040000==> #tx : 240
    ## Receive block : 7, gasLimit : 5959115, gasUsed : 5040000==> #tx : 240
    ## Receive block : 8, gasLimit : 5953297, gasUsed : 5019000==> #tx : 239
    ## Receive block : 9, gasLimit : 5947485, gasUsed : 5019000==> #tx : 239
    ## Receive block : 10, gasLimit : 5941678, gasUsed : 5019000==> #tx : 239


    ## Receive block : 0, gasLimit : 700000000, gasUsed : 0==> #tx : 0
    ## Receive block : 1, gasLimit : 699316408, gasUsed : 11592000==> #tx : 552
    ## Receive block : 2, gasLimit : 698633483, gasUsed : 15225000==> #tx : 725
    ## Receive block : 3, gasLimit : 697951225, gasUsed : 12432000==> #tx : 592
    ## Receive block : 4, gasLimit : 697269634, gasUsed : 9009000==> #tx : 429
    ## Receive block : 5, gasLimit : 696588708, gasUsed : 13671000==> #tx : 651
    ## Receive block : 6, gasLimit : 695908447, gasUsed : 2226000==> #tx : 106
    ## Receive block : 7, gasLimit : 695228850, gasUsed : 14280000==> #tx : 680
    ## Receive block : 8, gasLimit : 694549917, gasUsed : 11004000==> #tx : 524
    ## Receive block : 9, gasLimit : 693871647, gasUsed : 14784000==> #tx : 704
    ## Receive block : 10, gasLimit : 693194039, gasUsed : 5544000==> #tx : 264
     */

    @Test
    public void txCount() throws Exception {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        BigInteger start = BigInteger.ZERO;
        BigInteger last = web3j.ethBlockNumber().send().getBlockNumber();
        BigInteger prevGasLimit = BigInteger.ZERO;

        while (start.compareTo(last) <= 0) {
            Block block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(start), false).send().getBlock();
            SimpleLogger.println("## Receive block : {}, gasLimit : {}, gasUsed : {}, calc : {}, gasLimit Delta : {} ==> #tx : {}"
                , start, block.getGasLimit(), block.getGasUsed(), newGasLimit(prevGasLimit.longValue(), block), block.getGasLimit().subtract(prevGasLimit), block.getTransactions().size());
            start = start.add(BigInteger.ONE);
            prevGasLimit = block.getGasLimit();
        }
    }

    public long newGasLimit(long prevGasLimit, Block block) {
        long gasLimit = prevGasLimit;
        long gasUsed = block.getGasUsed().longValue();

        BigInteger newGasLimit = BigInteger.valueOf(gasLimit).multiply(BigInteger.valueOf(1023));
        return newGasLimit.multiply(BigInteger.valueOf(6)).divide(BigInteger.valueOf(5)).divide(BigInteger.valueOf(1024)).longValue();
    }
}
