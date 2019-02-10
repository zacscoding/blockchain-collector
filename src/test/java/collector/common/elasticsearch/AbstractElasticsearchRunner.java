package collector.common.elasticsearch;

import collector.configuration.ElasticsearchConfiguration;
import collector.configuration.properties.ElasticProperties;
import java.util.Arrays;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;

/**
 * @GitHub : https://github.com/zacscoding
 */
public class AbstractElasticsearchRunner extends EmbeddedElasticsearchTestRunner {

    protected static ElasticProperties properties;
    protected static ElasticsearchConfiguration config;
    protected static RestHighLevelClient restHighLevelClient;
    protected static ElasticsearchRestTemplate template;

    @BeforeClass
    public static void classSetUp() throws Exception {
        EmbeddedElasticsearchTestRunner.classSetUp();

        properties = new ElasticProperties();
        properties.setConnectTimeout(5);
        properties.setSocketTimeout(3);
        properties.setHosts(Arrays.asList("127.0.0.1:9200"));
        config = new ElasticsearchConfiguration(properties);

        template = config.elasticsearchRestTemplate();
        restHighLevelClient = template.getClient();
    }

    protected void deleteIndices() throws Exception {
        DeleteIndexRequest request = new DeleteIndexRequest("_all");
        restHighLevelClient.indices().delete(request);
    }
}
