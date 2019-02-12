package collector.common.elasticsearch;

import java.io.IOException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;


/**
 * @GitHub : https://github.com/zacscoding
 */
@Ignore
public class EmbeddedElasticsearchTestRunner {

    protected static RestHighLevelClient restHighLevelClient;

    @BeforeClass
    public static void classSetUp() throws Exception {
        EmbeddedElasticsearch.INSTANCE.start();
        /*restHighLevelClient = EmbeddedElasticsearch.INSTANCE.getRestHighLevelClient();
        DeleteIndexRequest request = new DeleteIndexRequest("_all");
        restHighLevelClient.indices().delete(request);*/
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        try {
            EmbeddedElasticsearch.INSTANCE.stop();
            /*DeleteIndexRequest request = new DeleteIndexRequest("_all");
            restHighLevelClient.indices().delete(request);*/
        } catch (Exception e) {
            if (!(e instanceof IOException)) {
                throw e;
            }
        }
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
}
