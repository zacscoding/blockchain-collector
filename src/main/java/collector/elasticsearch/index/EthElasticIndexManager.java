package collector.elasticsearch.index;

import collector.configuration.EthElasticConfiguration;
import collector.configuration.properties.EthElasticProperties;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.elasticsearch.ElasticsearchException;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j(topic = "es.index")
@ConditionalOnBean(EthElasticConfiguration.class)
@Component
public class EthElasticIndexManager {

    private final String defaultType = "_doc";
    private EthElasticProperties properties;
    private ElasticsearchRestTemplate template;

    @Autowired
    public EthElasticIndexManager(EthElasticProperties properties, ElasticsearchRestTemplate template) {
        this.properties = properties;
        this.template = template;
    }

    /**
     * Create index and put mappings if not exist
     *
     * @return true : success to create index & put mapping, otherwise false
     */
    public boolean createIndexAndPutMappingIfNotExist(String indexName, Class<?> clazz) {
        boolean indexResult = createIndexIfNotExist(indexName, clazz);

        if (!indexResult) {
            return false;
        }

        boolean existMapping = false;
        try {
            existMapping = template.getMapping(indexName, defaultType) == null;
        } catch (ElasticsearchException e) {
            existMapping = false;
        }

        return existMapping ? true : putMapping(indexName, clazz);
    }


    /**
     * Create index with settings
     *
     * @return true if success or already exist, otherwise false
     */
    public boolean createIndexIfNotExist(String indexName, Class<?> clazz) {
        Objects.requireNonNull(indexName, "indexName must be not null");

        if (!StringUtils.hasText(indexName)) {
            return false;
        }

        if (template.indexExists(indexName)) {
            return true;
        }

        try {
            // create index with settings
            String settings = getSettings(clazz);

            if (StringUtils.hasText(settings)) {
                return template.createIndex(indexName, settings);
            } else {
                return template.createIndex(indexName);
            }
        } catch (Exception e) {
            if (e instanceof ElasticsearchStatusException) {
                ElasticsearchStatusException statusException = (ElasticsearchStatusException) e;
                if (statusException.status().getStatus() == RestStatus.CONFLICT.getStatus()) {
                    return true;
                }
            }
            logger.error("Exception occur while create index : {}", indexName, e);
            return false;
        }
    }

    /**
     * Put mappings with "_doc" type
     *
     * @return true if success, not exist @Mappings, otherwise false
     */
    public boolean putMapping(String indexName, Class<?> clazz) {
        Objects.requireNonNull(indexName, "indexName must be not null");

        // put mappings
        String mappings = getMappings(clazz);
        if (!StringUtils.hasText(mappings)) {
            logger.warn("Skip mapping from {} clazz", clazz.getName());
            return true;
        }

        PutMappingRequest request = new PutMappingRequest();
        request.indices(indexName);
        request.type(defaultType);
        request.source(mappings, XContentType.JSON);

        try {
            return template.getClient().indices().putMapping(request, RequestOptions.DEFAULT).isAcknowledged();
        } catch (Exception e) {
            return false;
        }

        // version conflict ?
        // java.lang.NoSuchMethodError: org.elasticsearch.client.IndicesClient.putMapping(Lorg/elasticsearch/action/admin/indices/mapping/put/PutMappingRequest;[Lorg/apache/http/Header;)Lorg/elasticsearch/action/support/master/AcknowledgedResponse;
        // return template.putMapping(indexName, "_doc", mappings);
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

    public String getDefaultType() {
        return defaultType;
    }

    /**
     * Get settings string from clazz`s @Settings annotation
     */
    private String getSettings(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        if (!clazz.isAnnotationPresent(Setting.class)) {
            return null;
        }

        String settingPath = clazz.getAnnotation(Setting.class).settingPath();
        if (!StringUtils.hasText(settingPath)) {
            return null;
        }

        return template.readFileFromClasspath(settingPath);
    }

    private String getMappings(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        if (!clazz.isAnnotationPresent(Mapping.class)) {
            return null;
        }

        String mappingPath = (clazz.getAnnotation(Mapping.class)).mappingPath();
        if (!StringUtils.hasText(mappingPath)) {
            return null;
        }

        return template.readFileFromClasspath(mappingPath);
    }

    private String getRollingIndex(String prefix, long blockNumber, int rollingRange) {
        if (!properties.isRollingRule()) {
            return prefix;
        }

        long suffix = blockNumber - (blockNumber % rollingRange);

        return (prefix + "-" + suffix).toLowerCase();
    }
}
