package collector.ethereum.elasticsearch.index;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import collector.configuration.properties.EthereumElasticProperties;
import org.junit.Test;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

/**
 * @GitHub : https://github.com/zacscoding
 */
public class EthereumElasticIndexManagerTest {

    @Test
    public void test_notUseRollingIndex() {
        // given
        EthereumElasticProperties properties = new EthereumElasticProperties();
        properties.setRollingRule(false);
        EthereumElasticIndexManager indexManager = new EthereumElasticIndexManager(
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
        EthereumElasticProperties properties = new EthereumElasticProperties();
        properties.setRollingRule(true);
        properties.setRollingBlockNumber(100);
        properties.setRollingTxNumber(10);
        EthereumElasticIndexManager indexManager = new EthereumElasticIndexManager(
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
