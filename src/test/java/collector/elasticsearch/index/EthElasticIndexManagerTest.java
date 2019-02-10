package collector.elasticsearch.index;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.mockito.Mockito.mock;

import collector.common.elasticsearch.AbstractElasticsearchRunner;
import collector.configuration.properties.EthElasticProperties;
import collector.elasticsearch.EthElasticBlockEntity;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

/**
 * @GitHub : https://github.com/zacscoding
 */
public class EthElasticIndexManagerTest extends AbstractElasticsearchRunner {

    EthElasticProperties ethElasticProperties;
    EthElasticIndexManager ethElasticIndexManager;

    @Before
    public void setUp() throws Exception {
        super.deleteIndices();
        ethElasticProperties = new EthElasticProperties();
        ethElasticProperties.setRollingRule(false);
        ethElasticIndexManager = new EthElasticIndexManager(ethElasticProperties, template);
    }

    @Test
    public void test_createIndexWithSettings() {
        // given
        String indexNames = "test-index";

        // when
        boolean indexCreated = ethElasticIndexManager.createIndexIfNotExist(indexNames, EthElasticBlockEntity.class);

        // then
        assertThat(indexCreated).isTrue();
        Map settings = template.getSetting(indexNames);
        assertThat(settings.get("index.number_of_shards")).isEqualTo("3");
        assertThat(settings.get("index.number_of_replicas")).isEqualTo("1");
        assertThat(settings.get("index.refresh_interval")).isEqualTo("2s");
    }

    @Test
    public void test_createIndexWithNoSettings() {
        String indexName = "no-settings";

        boolean indexCreated = ethElasticIndexManager.createIndexIfNotExist(indexName, null);

        // then
        assertThat(indexCreated).isTrue();
        // default settings
        Map settings = template.getSetting(indexName);
        assertThat(settings.get("index.number_of_shards")).isEqualTo("5");
        assertThat(settings.get("index.number_of_replicas")).isEqualTo("1");
    }

    // TODO :: test again
    @Test
    public void test_putMappings() {
        // given
        String indexName = "new-blocks";
        assertThat(template.createIndex(indexName)).isTrue();

        // when
        boolean mappingsResult = ethElasticIndexManager.putMapping(indexName, EthElasticBlockEntity.class);

        // then
        assertThat(mappingsResult).isTrue();
        Map mappings = template.getMapping(indexName, "_doc");
        System.out.println(">> Mappings :: " + mappings);
    }

    @Test
    public void test_notUseRollingIndex() {
        // given
        EthElasticProperties properties = new EthElasticProperties();
        properties.setRollingRule(false);
        EthElasticIndexManager indexManager = new EthElasticIndexManager(
            properties, mock(ElasticsearchRestTemplate.class)
        );

        // when
        String blockIndex = indexManager.getBlockIndex("private", 50000);
        // then
        assertThat(blockIndex).isEqualTo("private-blocks");

        // when
        String txIndex = indexManager.getTxIndex("private", 50000);
        assertThat(txIndex).isEqualTo("private-transactions");
    }

    @Test
    public void test_useRollingIndex() {
        // given
        EthElasticProperties properties = new EthElasticProperties();
        properties.setRollingRule(true);
        properties.setRollingBlockNumber(100);
        properties.setRollingTxNumber(10);
        EthElasticIndexManager indexManager = new EthElasticIndexManager(
            properties, mock(ElasticsearchRestTemplate.class)
        );

        // when then
        assertThat(indexManager.getBlockIndex("private", 0)).isEqualTo("private-blocks-0");
        assertThat(indexManager.getBlockIndex("private", 99)).isEqualTo("private-blocks-0");
        assertThat(indexManager.getBlockIndex("private", 100)).isEqualTo("private-blocks-100");
        assertThat(indexManager.getBlockIndex("private", 201)).isEqualTo("private-blocks-200");

        assertThat(indexManager.getTxIndex("private", 0)).isEqualTo("private-transactions-0");
        assertThat(indexManager.getTxIndex("private", 9)).isEqualTo("private-transactions-0");
        assertThat(indexManager.getTxIndex("private", 10)).isEqualTo("private-transactions-10");
        assertThat(indexManager.getTxIndex("private", 21)).isEqualTo("private-transactions-20");
    }
}
