package demo.configuration;

import demo.entity.EthNode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author zacconding
 * @Date 2018-07-19
 * @GitHub : https://github.com/zacscoding
 */
@Component
@ConfigurationProperties(prefix = "collector")
@Scope("prototype")
public class CollectorProperties {

    private List<EthNode> nodes = new ArrayList<>();

    public CollectorProperties() {
        System.out.println("## CollectorProperties is created..");
    }

    public List<EthNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<EthNode> nodes) {
        this.nodes = nodes;
    }
}