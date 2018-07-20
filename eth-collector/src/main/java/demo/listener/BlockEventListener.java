package demo.listener;

import demo.entity.EthNode;
import java.math.BigInteger;
import org.web3j.protocol.core.methods.response.EthBlock.Block;

/**
 * @author zacconding
 * @Date 2018-07-19
 * @GitHub : https://github.com/zacscoding
 */
public interface BlockEventListener {

    void onBlock(EthNode ethNode, Block block);

    void onBlock(EthNode ethNode, BigInteger blockNumber);
}