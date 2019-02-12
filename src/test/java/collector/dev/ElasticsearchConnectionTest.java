package collector.dev;

import collector.configuration.ElasticsearchConfiguration;
import collector.configuration.properties.ElasticProperties;
import java.util.Arrays;
import org.elasticsearch.action.main.MainResponse;
import org.elasticsearch.client.RequestOptions;
import org.junit.Test;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

/**
 * @GitHub : https://github.com/zacscoding
 */
public class ElasticsearchConnectionTest {

    @Test
    public void testElasticConnection() {
        ElasticProperties properties = new ElasticProperties();
        properties.setConnectTimeout(5);
        properties.setSocketTimeout(3);
        properties.setHosts(Arrays.asList("192.168.5.78:9200"));

        ElasticsearchConfiguration config = new ElasticsearchConfiguration(properties);
        ElasticsearchRestTemplate template = config.elasticsearchRestTemplate();

        try {
            MainResponse response = template.getClient().info(RequestOptions.DEFAULT);
            System.out.println(template.indexExists("Private-Newtwork"));
            System.out.println(template.getMapping("temp-index", "_doc"));
            System.out.println("Success to connect. node name : " + response.getNodeName());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
