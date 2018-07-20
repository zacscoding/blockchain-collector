package demo.entity;

/**
 * @author zacconding
 * @Date 2018-07-19
 * @GitHub : https://github.com/zacscoding
 */
public class EthNode {

    private String name;
    private String type;
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "EthNode{" + "name='" + name + '\'' + ", type='" + type + '\'' + ", url='" + url + '\'' + '}';
    }
}
