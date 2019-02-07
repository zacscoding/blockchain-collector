package collector.ethereum.elasticsearch.parser;

import collector.ethereum.elasticsearch.EthereumElasticBlockEntity;
import collector.ethereum.elasticsearch.EthereumElasticLogEntity;
import collector.ethereum.elasticsearch.EthereumElasticTxEntity;
import collector.util.CollectionUtil;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionObject;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionResult;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;
import org.web3j.utils.Numeric;

/**
 * Parse Ethereum to Elasticsearch entity
 *
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j(topic = "parser.elastic")
public class EthereumElasticParser {

    public static EthereumElasticParser INSTANCE = new EthereumElasticParser();

    private EthereumElasticParser() {
    }

    /**
     * Parse web3j block response to elasticsearch entity
     */
    public EthereumElasticBlockEntity parseBlock(Block block) {
        if (block == null) {
            return null;
        }

        List<String> txnsHashes = null;
        String txFees = null;

        if (CollectionUtil.isEmpty(block.getTransactions())) {
            txnsHashes = Collections.emptyList();
            txFees = "0";
        } else {
            List<TransactionResult> txResults = block.getTransactions();
            txnsHashes = new ArrayList<>(txResults.size());
            BigDecimal blockTxFees = new BigDecimal(0);

            Class<?> txResultClass = block.getTransactions().get(0).getClass();

            if (txResultClass.isAssignableFrom(Transaction.class)) {
                for (TransactionResult txResult : txResults) {
                    Transaction tx = (Transaction) txResult.get();
                    txnsHashes.add(tx.getHash());
                    String txPriceVal = calculateTxPrice(tx.getGas(), tx.getGasPrice());
                    if (StringUtils.hasText(txPriceVal)) {
                        blockTxFees = blockTxFees.add(new BigDecimal(txPriceVal));
                    }
                }
            } else if (txResultClass.isAssignableFrom(String.class)) {
                for (TransactionResult txResult : txResults) {
                    txnsHashes.add((String) txResult.get());
                }
            } else if (txResultClass.isAssignableFrom(TransactionObject.class)) {
                for (TransactionResult txResult : txResults) {
                    TransactionObject txObject = (TransactionObject) txResult.get();
                    txnsHashes.add(txObject.getHash());
                    String txPriceVal = calculateTxPrice(txObject.getGas(), txObject.getGasPrice());
                    if (StringUtils.hasText(txPriceVal)) {
                        blockTxFees = blockTxFees.add(new BigDecimal(txPriceVal));
                    }
                }
            } else {
                logger.warn("Cannot cast TransactionResult class. class : {}", txResultClass.getName());
                txnsHashes = Collections.emptyList();
                txFees = "0";
            }
        }

        return EthereumElasticBlockEntity.builder()
            .hash(block.getHash())
            .number(block.getNumber().longValue())
            .parentHash(block.getParentHash())
            .nonce(block.getNonceRaw())
            .sha3Uncles(block.getSha3Uncles())
            .miner(block.getMiner())
            .difficulty(block.getDifficultyRaw())
            .extraData(block.getExtraData())
            .size(block.getSizeRaw())
            .gasLimit(block.getGasLimitRaw())
            .gasUsed(block.getGasUsedRaw())
            .timestamp(block.getTimestamp().longValue())
            .receiptRoot(block.getReceiptsRoot())
            .stateRoot(block.getStateRoot())
            .transactionsRoot(block.getTransactionsRoot())
            .logsBloom(block.getLogsBloom())
            .mixHash(block.getMixHash())
            .txFees(txFees)
            .transactions(txnsHashes)
            .uncles(block.getUncles())
            .sealFields(block.getSealFields())
            .txCount(CollectionUtil.safeGetSize(block.getTransactions()))
            .build();
    }

    /**
     * Parse web3j transaction, transaction receipt response to elasticsearch entity
     */
    public EthereumElasticTxEntity parseTransaction(Transaction tx, TransactionReceipt tr, long timestamp) {
        if (tx == null || tr == null) {
            return null;
        }

        return EthereumElasticTxEntity.builder()
            .hash(tx.getHash())
            .nonce(tx.getNonceRaw())
            .blockHash(tx.getBlockHash())
            .blockNumber(tx.getBlockNumber().longValue())
            .transactionIndex(tx.getTransactionIndex().intValue())
            .from(tx.getFrom())
            .to(tx.getTo())
            .value(tx.getValueRaw())
            .gasPrice(tx.getGasPriceRaw())
            .gas(tx.getGasRaw())
            .txPrice(null)
            .input(tx.getInput())
            .timestamp(timestamp)
            .cumulativeGasUsed(tr.getCumulativeGasUsedRaw())
            .contractAddress(tr.getContractAddress())
            .status(tr.getStatus())
            .logs(parseLogs(tr))
            .build();
    }

    public List<EthereumElasticLogEntity> parseLogs(TransactionReceipt tr) {
        if (tr == null || CollectionUtil.isEmpty(tr.getLogs())) {
            return Collections.emptyList();
        }

        return tr.getLogs().stream().map(
            log -> EthereumElasticLogEntity.builder()
                .removed(log.isRemoved())
                .logIndex(log.getLogIndexRaw())
                .data(splitLogData(log.getData()))
                .type(log.getType())
                .topics(log.getTopics())
                .build()
        ).collect(Collectors.toList());
    }

    private String calculateTxPrice(BigInteger gas, BigInteger gasPrice) {
        if (gas == null || gasPrice == null) {
            return null;
        }

        return Convert.fromWei(gas.multiply(gasPrice).toString(), Unit.ETHER).toPlainString();
    }

    /**
     * Split log data length by 64
     */
    private List<String> splitLogData(String logData) {
        if (!StringUtils.hasText(logData) || logData.equals("0x")) {
            return Collections.emptyList();
        } else {
            final int dataLength = 64;
            // clean hex prefix
            logData = Numeric.cleanHexPrefix(logData);
            List<String> result = new ArrayList<>((int) Math.ceil(logData.length() / dataLength));

            for (int i = 0; i < logData.length() - dataLength + 1; i += dataLength) {
                result.add(logData.substring(i, i + 64));
            }

            return result;
        }
    }
}
