package collector.elasticsearch;

import static org.assertj.core.api.Assertions.assertThat;

import collector.elasticsearch.parser.EthElasticParser;
import org.junit.Test;

/**
 * TODO :: parser test
 *
 * @GitHub : https://github.com/zacscoding
 */
public class EthElasticParserTest {

    @Test
    public void test_parseBlock() {
        // when then
        assertThat(EthElasticParser.INSTANCE.parseBlock(null)).isNull();

        // TODO :: sample json file & test code
    }
}