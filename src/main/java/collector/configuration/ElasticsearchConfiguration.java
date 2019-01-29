package collector.configuration;

import collector.configuration.properties.ElasticProperties;
import java.time.Duration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

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
        String[] hostUrls = new String[properties.getHosts().size()];
        properties.getHosts().toArray(hostUrls);

        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
            .connectedTo(hostUrls)
            .withConnectTimeout(Duration.ofSeconds(properties.getConnectTimeout()))
            .withSocketTimeout(Duration.ofSeconds(properties.getSocketTimeout()))
            .build();

        return RestClients.create(clientConfiguration).rest();
    }

    @Bean
    public ElasticsearchRestTemplate elasticsearchRestTemplate() {
        return new ElasticsearchRestTemplate(restHighLevelClient());
    }
}
