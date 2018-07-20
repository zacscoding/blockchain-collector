//package demo.config;
//
//import com.typesafe.config.Config;
//import com.typesafe.config.ConfigFactory;
//import com.typesafe.config.ConfigList;
//import com.typesafe.config.ConfigObject;
//import demo.entity.EthNode;
//import java.util.ArrayList;
//import java.util.List;
//import org.junit.Test;
//
///**
// * @author zacconding
// * @Date 2018-07-19
// * @GitHub : https://github.com/zacscoding
// */
//public class ReadProperties {
//
//    @Test
//    public void readTest() {
//        List<EthNode> ethNodes = new ArrayList<>();
//
//        Config config = ConfigFactory.parseResources("test-app.conf");
//        if (config.hasPath("collector.nodes")) {
//            List<? extends ConfigObject> configObjects = config.getObjectList("collector.nodes");
//            for (ConfigObject configObject : configObjects) {
//                if (configObject.get("nodeName") == null) {
//                    System.out.println("## node name is null!");
//                    continue;
//                }
//
//                if (configObject.get("url") == null) {
//                    System.out.println("## url is null!");
//                    continue;
//                }
//
//                if (configObject.get("type") == null) {
//                    System.out.println("## type is null!");
//                    continue;
//                }
//
//                String nodeName = configObject.toConfig().getString("nodeName");
//                String url = configObject.toConfig().getString("url");
//                String type = configObject.toConfig().getString("type");
//
//
//                // ethNodes.add(new EthNode(nodeName, type, url));
//            }
//        }
//
//        System.out.println("## pared : " + ethNodes.size());
//        for (EthNode ethNode : ethNodes) {
//            System.out.println(ethNode);
//        }
//    }
//}
