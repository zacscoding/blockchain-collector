package collector.ethereum.elasticsearch;

import org.junit.Before;
import org.junit.Test;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

/**
 * TODO :: parser test
 * @GitHub : https://github.com/zacscoding
 */
public class EthereumElasticParserTest {

    private Block defaultBlock;
    private Transaction defaultTransaction;
    private TransactionReceipt defaultTransactionReceipt;

    @Before
    public void setUp() {

    }

    @Test
    public void test_parseBlock() {
    }
}