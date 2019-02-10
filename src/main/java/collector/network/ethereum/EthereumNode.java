package collector.network.ethereum;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zacconding
 * @Date 2018-12-19
 * @GitHub : https://github.com/zacscoding
 */
@Getter
@Setter
public class EthereumNode {

    // ethereum node name
    private String nodeName;
    // ethereum json rpc http url
    private String httpUrl;
    // ethereum ipc path
    private String ipcPath;
    // ethereum websocket url
    private String webSocketUrl;
}