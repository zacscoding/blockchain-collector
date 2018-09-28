package blockchain.message.parser;

import blockchain.message.wrapper.ethereum.EthereumBlockWrapper;
import blockchain.message.wrapper.ethereum.EthereumTransactionWrapper;
import blockchain.model.BlockchainNode;
import java.nio.channels.WritePendingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.util.CollectionUtils;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionObject;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionResult;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

/**
 * @author zacconding
 * @Date 2018-09-29
 * @GitHub : https://github.com/zacscoding
 */
public class BlockchainParser {

    /**
     * Ethereum wrapper parser
     */
    public static class EthereumParser {

        /**
         * Parse Ethereum block + Blockchain Node to EthereumBlockWrapper
         */
        public static EthereumBlockWrapper parseBlock(BlockchainNode blockchainNode, Block block) {
            if (blockchainNode == null) {
                return null;
            }

            EthereumBlockWrapper wrapper = new EthereumBlockWrapper();
            wrapper.setNodeName(blockchainNode.getNodeName());

            if (block == null) {
                return wrapper;
            }

            wrapper.setNumber(block.getNumberRaw());
            wrapper.setHash(block.getHash());
            wrapper.setParentHash(block.getParentHash());
            wrapper.setNonce(block.getNonceRaw());
            wrapper.setSha3Uncles(block.getSha3Uncles());
            wrapper.setLogsBloom(block.getLogsBloom());
            wrapper.setTransactionsRoot(block.getTransactionsRoot());
            wrapper.setStateRoot(block.getStateRoot());
            wrapper.setReceiptsRoot(block.getReceiptsRoot());
            wrapper.setAuthor(block.getAuthor());
            wrapper.setMiner(block.getMiner());
            wrapper.setMixHash(block.getMixHash());
            wrapper.setDifficulty(block.getDifficultyRaw());
            wrapper.setTotalDifficulty(block.getTotalDifficultyRaw());
            wrapper.setExtraData(block.getExtraData());
            wrapper.setSize(block.getSizeRaw());
            wrapper.setGasLimit(block.getGasLimitRaw());
            wrapper.setGasUsed(block.getGasUsedRaw());
            wrapper.setTimestamp(block.getTimestampRaw());
            wrapper.setTransactions(extractTxHashes(block));
            wrapper.setUncles(block.getUncles());
            wrapper.setSealFields(block.getSealFields());

            return wrapper;
        }

        public static EthereumTransactionWrapper parseTransaction(BlockchainNode blockchainNode, Transaction tx, TransactionReceipt tr) {
            if (blockchainNode == null) {
                return null;
            }

            EthereumTransactionWrapper wrapper = new EthereumTransactionWrapper();
            wrapper.setNodeName(blockchainNode.getNodeName());

            if (tx == null && tr == null) {
                return wrapper;
            }

            if (tx != null) {
                wrapper.setHash(tx.getHash());
                wrapper.setNonce(tx.getNonceRaw());
                wrapper.setBlockHash(tx.getBlockHash());
                wrapper.setBlockNumber(tx.getBlockNumberRaw());
                wrapper.setTransactionIndex(tx.getTransactionIndexRaw());
                wrapper.setFrom(tx.getFrom());
                wrapper.setTo(tx.getTo());
                wrapper.setValue(tx.getValueRaw());
                wrapper.setGasPrice(tx.getGasPriceRaw());
                wrapper.setGas(tx.getGasRaw());
                wrapper.setInput(tx.getInput());
                wrapper.setCreates(tx.getCreates());
                wrapper.setPublicKey(tx.getPublicKey());
                wrapper.setRaw(tx.getRaw());
                wrapper.setR(tx.getR());
                wrapper.setS(tx.getS());
                wrapper.setV(String.valueOf(tx.getV()));
            } else if (tr != null) {
                wrapper.setHash(tr.getTransactionHash());
                wrapper.setTransactionIndex(tr.getTransactionIndexRaw());
                wrapper.setBlockHash(tr.getBlockHash());
                wrapper.setBlockNumber(tr.getBlockNumberRaw());
                wrapper.setGas(tr.getGasUsedRaw());
                wrapper.setFrom(tr.getFrom());
                wrapper.setTo(tr.getTo());
            }

            if (tr != null) {
                wrapper.setCumulativeGasUsed(tr.getCumulativeGasUsedRaw());
                wrapper.setStatus(tr.getStatus());
                wrapper.setLogsBloom(tr.getLogsBloom());
            }

            return wrapper;
        }

        private static List<String> extractTxHashes(Block block) {
            if (CollectionUtils.isEmpty(block.getTransactions())) {
                return Collections.emptyList();
            }

            Class<?> txClass = block.getTransactions().get(0).getClass();
            List<String> txns = new ArrayList<>(block.getTransactions().size());

            if (txClass.isAssignableFrom(String.class)) {
                for (TransactionResult<String> result : block.getTransactions()) {
                    txns.add(result.get());
                }
            } else if (txClass.isAssignableFrom(Transaction.class)) {
                for (TransactionResult<Transaction> result : block.getTransactions()) {
                    txns.add(result.get().getHash());
                }
            } else if (txClass.isAssignableFrom(TransactionObject.class)) {
                for (TransactionResult<TransactionObject> result : block.getTransactions()) {
                    txns.add(result.get().get().getHash());
                }
            } else {
                throw new RuntimeException("Invalid tx class type : " + txClass.getName());
            }

            return txns;
        }
    }
}
