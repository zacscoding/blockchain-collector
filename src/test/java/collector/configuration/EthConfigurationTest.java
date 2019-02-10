package collector.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import collector.common.elasticsearch.EmbeddedElasticsearch;
import collector.common.elasticsearch.EmbeddedElasticsearchTestRunner;
import collector.configuration.properties.ElasticProperties;
import java.util.Arrays;
import org.elasticsearch.action.main.MainResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

/**
 * @GitHub : https://github.com/zacscoding
 */
public class EthConfigurationTest extends EmbeddedElasticsearchTestRunner {

    private ElasticProperties properties;
    private ElasticsearchConfiguration config;

    @Before
    public void setUp() {
        properties = new ElasticProperties();
        properties.setConnectTimeout(5);
        properties.setSocketTimeout(3);
        properties.setHosts(Arrays.asList("127.0.0.1:9200"));

        config = new ElasticsearchConfiguration(properties);
    }

    @Test
    public void temp() throws Exception {
        RestHighLevelClient client = EmbeddedElasticsearch.INSTANCE.getRestHighLevelClient();
        MainResponse response = client.info(RequestOptions.DEFAULT);
        System.out.println(response.toString());
    }

    @Test
    public void test_restHighLevelClient() throws Exception {
        // when
        RestHighLevelClient client = config.restHighLevelClient();

        // then
        assertThat(client).isNotNull();
        MainResponse response = client.info(RequestOptions.DEFAULT);
        assertThat(response.isAvailable()).isTrue();
    }

    @Test
    public void test_elasticsearchRestTemplate() {
        // when
        ElasticsearchRestTemplate template = config.elasticsearchRestTemplate();

        // then
        assertThat(template).isNotNull();
        assertThat(template.indexExists("test_index")).isFalse();
    }
}
