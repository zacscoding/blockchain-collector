package collector.ethereum.elasticsearch;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @GitHub : https://github.com/zacscoding
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EthereumElasticLogEntity {

    private boolean removed;
    private String logIndex;
    private List<String> data;
    private String type;
    private List<String> topics;
}
