package collector.ethereum.elasticsearch;

import static org.assertj.core.api.Assertions.assertThat;

import collector.ethereum.elasticsearch.parser.EthereumElasticParser;
import org.junit.Test;

/**
 * TODO :: parser test
 *
 * @GitHub : https://github.com/zacscoding
 */
public class EthereumElasticParserTest {

    @Test
    public void test_parseBlock() {
        // when then
        assertThat(EthereumElasticParser.INSTANCE.parseBlock(null)).isNull();

        // TODO :: sample json file & test code
    }
}