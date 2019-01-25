package collector.configuration;

import collector.configuration.properties.ElasticProperties;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.web.client.RestTemplate;

/**
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j(topic = "config")
@ConditionalOnProperty(value = "elasticsearch.enabled", havingValue = "true")
@Configuration
public class ElasticsearchConfiguration {

    private ElasticProperties properties;

    @Autowired
    public ElasticsearchConfiguration(ElasticProperties properties) {
        this.properties = properties;
    }

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        List<String> hostUrls = properties.getHosts();
        HttpHost[] hosts = new HttpHost[hostUrls.size()];

        for (int i = 0; i < hostUrls.size(); i++) {
            hosts[i] = new HttpHost(hostUrls.get(i));
        }

        return new RestHighLevelClient(RestClient.builder(hosts));
    }

    @Bean
    public ElasticsearchRestTemplate elasticsearchRestTemplate() {
        return new ElasticsearchRestTemplate(restHighLevelClient());
    }
}
