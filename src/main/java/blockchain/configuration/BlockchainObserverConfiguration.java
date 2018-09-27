package blockchain.configuration;

import blockchain.message.EthereumMessageProducer;
import blockchain.model.BlockchainNode;
import blockchain.model.enums.BlockchainType;
import blockchain.observe.listener.BlockchainListener;
import blockchain.observe.listener.EthereumListener;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.CollectionUtils;

/**
 * @author zacconding
 * @Date 2018-09-26
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j(topic = "observe")
@Configuration
public class BlockchainObserverConfiguration {

    @Bean
    public BlockchainListener blockchainListener() {
        List<EthereumListener> ethereumListeners = Arrays.asList(new EthereumMessageProducer());
        return new BlockchainListener(ethereumListeners);
    }

    @Bean
    public List<BlockchainNode> blockchainNodes() {
        return readBlockchainNodes();
    }

    private List<BlockchainNode> readBlockchainNodes() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = null;
            String configFile = System.getProperty("observer.config.file");
            if (configFile != null) {
                rootNode = objectMapper.readTree(new File(configFile));
            } else {
                configFile = "observer-config.json";
                rootNode = objectMapper.readTree(new ClassPathResource("observer-config.json").getInputStream());
            }

            if (!rootNode.has("networks")) {
                throw new Exception("networks field must be not null");
            }

            JsonNode networksJsonNode = rootNode.get("networks");
            if (!networksJsonNode.isArray()) {
                throw new Exception("Networks value must be array");
            }

            List<BlockchainNode> blockchainNodes = new ArrayList<>();

            for (JsonNode networkNode : networksJsonNode) {
                BlockchainType blockchainType = BlockchainType.getType(networkNode.get("blockchainType").asText());
                switch (blockchainType) {
                    case ETHEREUM:
                        readEthereumNodes(blockchainNodes, objectMapper, networkNode);
                        break;
                    case BITCOIN:
                        throw new UnsupportedOperationException("Not supported yet bitcoin node");
                    case UNKNOWN:
                    default:
                        throw new Exception("Invalid blockchain type : " + networkNode.get("blockchainType"));
                }
            }

            if (CollectionUtils.isEmpty(blockchainNodes)) {
                throw new RuntimeException("Empty blockchain observer.. : " + configFile);
            }

            return blockchainNodes;
        } catch (Exception e) {
            log.warn("Failed to read observe.json file", e);
            throw new RuntimeException(e);
        }
    }

    private void readEthereumNodes(List<BlockchainNode> blockchainNodes, ObjectMapper objectMapper, JsonNode ethereumNodes) throws Exception {
        long blockTime = ethereumNodes.get("blockTime").asLong();

        for (JsonNode ethereumNode : ethereumNodes.get("blockchainNodes")) {

            BlockchainNode blockchainNode = objectMapper.treeToValue(ethereumNode, BlockchainNode.class);

            if (isDuplicateNodeName(blockchainNodes, blockchainNode.getNodeName())) {
                throw new Exception("Duplicate node name : " + blockchainNode.getNodeName());
            }

            blockchainNode.setBlockchainType(BlockchainType.ETHEREUM);
            blockchainNode.setBlockTime(blockTime);

            blockchainNodes.add(blockchainNode);
            if (log.isTraceEnabled()) {
                log.trace("> Success to read blockchain node : \n" + blockchainNode);
            }
        }
    }

    private boolean isDuplicateNodeName(List<BlockchainNode> blockchainNodes, String nodeName) {
        for (BlockchainNode blockchainNode : blockchainNodes) {
            if (blockchainNode.getNodeName().equalsIgnoreCase(nodeName)) {
                return true;
            }
        }

        return false;
    }
}