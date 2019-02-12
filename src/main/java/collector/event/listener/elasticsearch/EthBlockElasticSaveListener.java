package collector.event.listener.elasticsearch;

import collector.configuration.EthConfiguration;
import collector.configuration.EthElasticConfiguration;
import collector.elasticsearch.EthElasticBlockEntity;
import collector.elasticsearch.index.EthElasticIndexManager;
import collector.elasticsearch.parser.EthElasticParser;
import collector.event.EthBlockEvent;
import collector.event.publisher.EthBlockPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.GetQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.EthBlock.Block;

/**
 * Listen block event & Save block entity to elasticsearch
 *
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j(topic = "listener")
@ConditionalOnBean(value = {EthConfiguration.class, EthElasticConfiguration.class})
@Component
public class EthBlockElasticSaveListener {

    private ElasticsearchRestTemplate template;
    private EthElasticParser parser;
    private EthElasticIndexManager indexManager;
    private ObjectMapper objectMapper;

    @Autowired
    public EthBlockElasticSaveListener(EthBlockPublisher blockPublisher,
        ElasticsearchRestTemplate template,
        EthElasticIndexManager indexManager,
        ObjectMapper objectMapper) {

        this.template = template;
        this.indexManager = indexManager;
        this.parser = EthElasticParser.INSTANCE;
        this.objectMapper = objectMapper;
        blockPublisher.register(this);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onBlock(EthBlockEvent blockEvent) {
        String networkName = blockEvent.getNetworkName();
        Block block = blockEvent.getBlock();

        String indexName = indexManager.getBlockIndex(blockEvent.getNetworkName(), block.getNumber().longValue());
        boolean index = indexManager.createIndexAndPutMappingIfNotExist(indexName, EthElasticBlockEntity.class);
        if (!index) {
            logger.error("Failed to create index {} & put mappings. so skip saving block.", indexName);
            return;
        }

        // todo :: check exist after added search api
        try {
            IndexQuery indexQuery = new IndexQuery();
            indexQuery.setIndexName(indexName);
            indexQuery.setId(block.getHash());
            indexQuery.setType(indexManager.getDefaultType());
            indexQuery.setSource(objectMapper.writeValueAsString(
                parser.parseBlock(block)
            ));

            String result = template.index(indexQuery);
            logger.info("Success to save block {} - {}", networkName, result);
        } catch (Exception e) {
            logger.error("Failed to save block. block event : {}", blockEvent, e);
        }
    }
}
