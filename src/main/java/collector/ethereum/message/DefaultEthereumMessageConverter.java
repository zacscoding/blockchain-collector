package collector.ethereum.message;

import collector.configuration.EthereumConfiguration;
import collector.ethereum.event.EthereumBlockEvent;
import collector.ethereum.event.EthereumPendingTxEvent;
import collector.ethereum.event.EthereumTxEvent;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

/**
 * Convert events to message with json format
 *
 * can define convert class by impl EthereumMessageConverter
 *
 * @author zacconding
 * @Date 2018-12-20
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j(topic = "parser")
@Component
@ConditionalOnBean(value = EthereumConfiguration.class)
public class DefaultEthereumMessageConverter implements EthereumMessageConverter {

    @Override
    public String convertBlockEvent(EthereumBlockEvent blockEvent) {
        Block block = blockEvent.getBlock();
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);

        try {
            writer.beginObject();
            writer.name("metadata")
                    .beginObject()
                        .name("networkName").value(blockEvent.getNetworkName())
                        .name("nodeName").value(blockEvent.getEthereumNode().getNodeName())
                .endObject();

            writer.name("block")
                    .beginObject()
                        .name("number").value(block.getNumber())
                        .name("hash").value(block.getHash())
                        .name("parentHash").value(block.getParentHash())
                        .name("sha3Uncles").value(block.getSha3Uncles())
                        .name("logsBloom").value(block.getLogsBloom())
                        .name("transactionsRoot").value(block.getTransactionsRoot())
                        .name("stateRoot").value(block.getStateRoot())
                        .name("miner").value(block.getMiner())
                        .name("difficulty").value(block.getDifficultyRaw())
                        .name("totalDifficulty").value(block.getTotalDifficultyRaw())
                        .name("extraData").value(block.getExtraData())
                        .name("size").value(block.getSizeRaw())
                        .name("gasLimit").value(block.getGasLimitRaw())
                        .name("gasUsed").value(block.getGasUsedRaw())
                        .name("timestamp").value(block.getTimestampRaw());
            writeStringList(writer, "sealFields", block.getSealFields());
            writeStringList(writer, "uncles", block.getUncles());

            List<String> txHashes = block.getTransactions().stream().map(
                transactionResult -> ((Transaction) transactionResult.get()).getHash()
            ).collect(Collectors.toList());
            writeStringList(writer, "transactions", txHashes);

            writer.endObject()
                .endObject().flush();

            return sw.toString();
        } catch (Exception e) {
            logger.warn("Failed to convert block event to message", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String convertTxEvent(EthereumTxEvent txEvent) {
        Transaction tx = txEvent.getTransaction();
        TransactionReceipt tr = txEvent.getTransactionReceipt();

        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);

        try {
            writer.beginObject()
                    .name("metadata")
                        .beginObject()
                            .name("networkName").value(txEvent.getNetworkName())
                            .name("nodeName").value(txEvent.getEthereumNode().getNodeName())
                        .endObject()
                    .name("transaction")
                        .beginObject()
                            .name("hash").value(tx.getHash())
                            .name("nonce").value(tx.getNonceRaw())
                            .name("blockHash").value(tx.getBlockHash())
                            .name("blockNumber").value(tx.getBlockNumberRaw())
                            .name("transactionIndex").value(tx.getTransactionIndexRaw())
                            .name("from").value(tx.getFrom())
                            .name("to").value(tx.getTo())
                            .name("value").value(tx.getValueRaw())
                            .name("gas").value(tx.getGasRaw())
                            .name("gasPrice").value(tx.getGasPriceRaw())
                            .name("input").value(tx.getInput())
                            .name("contractAddress").value(tr.getContractAddress())
                            .name("cumulativeGasUsed").value(tr.getCumulativeGasUsedRaw())
                            .name("gasUsed").value(tr.getGasUsedRaw())
                            .name("logsBloom").value(tr.getLogsBloom())
                            .name("status").value(tr.getStatus());
            writeLogs(writer, tr.getLogs());

            writer.endObject()
                .endObject().flush();
            return sw.toString();
        } catch (Exception e) {
            logger.warn("Failed to convert tx event to message", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String convertPendingTxEvent(EthereumPendingTxEvent pendingTxEvent) {
        Transaction tx = pendingTxEvent.getPendingTx();
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);

        try {
            writer.beginObject()
                    .name("metadata")
                        .beginObject()
                            .name("networkName").value(pendingTxEvent.getNetworkName())
                            .name("nodeName").value(pendingTxEvent.getNodeName())
                        .endObject()
                    .name("pendingTransaction")
                        .beginObject()
                            .name("hash").value(tx.getHash())
                            .name("nonce").value(tx.getNonceRaw())
                            .name("from").value(tx.getFrom())
                            .name("to").value(tx.getTo())
                            .name("value").value(tx.getValueRaw())
                            .name("gas").value(tx.getGasRaw())
                            .name("gasPrice").value(tx.getGasPriceRaw())
                            .name("input").value(tx.getInput())
                        .endObject()
                .endObject().flush();

            return sw.toString();
        } catch (Exception e) {
            logger.warn("Failed to convert pending tx event to message", e);
            throw new RuntimeException(e);
        }
    }

    private void writeStringList(JsonWriter writer, String name, List<String> values) throws IOException {
        writer.name(name)
            .beginArray();
        for (String value : values) {
            writer.value(value);
        }
        writer.endArray();
    }

    private void writeLogs(JsonWriter writer, List<Log> logs) throws IOException {
        writer.name("logs").beginArray();
        for (Log log : logs) {
            writer.name("removed").value(log.isRemoved())
                .name("logIndex").value(log.getLogIndexRaw())
                .name("data").value(log.getData());

            writeStringList(writer, "topics", log.getTopics());
        }
        writer.endArray();
    }
}