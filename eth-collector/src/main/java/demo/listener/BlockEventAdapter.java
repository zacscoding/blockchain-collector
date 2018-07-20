package demo.listener;

import demo.entity.EthNode;
import java.math.BigInteger;
import org.web3j.protocol.core.methods.response.EthBlock.Block;

/**
 * @author zacconding
 * @Date 2018-07-19
 * @GitHub : https://github.com/zacscoding
 */
public class BlockEventAdapter implements BlockEventListener {

    @Override
    public void onBlock(EthNode ethNode, Block block) {
    }

    @Override
    public void onBlock(EthNode ethNode, BigInteger blockNumber) {
    }
}