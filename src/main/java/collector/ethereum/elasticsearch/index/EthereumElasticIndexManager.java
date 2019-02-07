package collector.ethereum.elasticsearch.index;

import collector.configuration.EthereumElasticConfiguration;
import collector.configuration.properties.EthereumElasticProperties;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Component;

/**
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j(topic = "es.index")
@ConditionalOnBean(EthereumElasticConfiguration.class)
@Component
public class EthereumElasticIndexManager {

    private EthereumElasticProperties properties;
    private ElasticsearchRestTemplate template;

    @Autowired
    public EthereumElasticIndexManager(EthereumElasticProperties properties, ElasticsearchRestTemplate template) {
        this.properties = properties;
        this.template = template;
    }


    private boolean createIndexIfNotExist(String indexName, Class<?> clazz) {
        Objects.requireNonNull(indexName, "indexName must be not null");

        if (template.indexExists(indexName)) {
            return true;
        }

        // TODO :: impl
        return false;
    }


    /**
     * Get block index
     *
     * @return {networkName}-{blocks} if not use rolling rule, otherwise {networkName-blocks}-{startNumber}
     */
    public String getBlockIndex(String networkName, long blockNumber) {
        return getRollingIndex(networkName + "-blocks", blockNumber, properties.getRollingBlockNumber());
    }


    /**
     * Get transactions index
     *
     * @return {networkName}-{transactions} if not use rolling rule, otherwise {networkName}-{transactions}-{startBlockNumber}
     */
    public String getTxIndex(String networkName, long blockNumber) {
        return getRollingIndex(networkName + "-transactions", blockNumber, properties.getRollingTxNumber());
    }

    private String getRollingIndex(String prefix, long blockNumber, int rollingRange) {
        if (!properties.isRollingRule()) {
            return prefix;
        }

        long suffix = blockNumber - (blockNumber % rollingRange);

        return prefix + "-" + suffix;
    }
}
